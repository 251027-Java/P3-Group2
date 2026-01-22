import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { AuthService } from '../../services/auth.service';

interface ListingResponse {
  listingId: number;
  ownerUserId: number;
  cardId: number;
  conditionRating: number;
  listingStatus: string;
  createdAt: string;
}

interface CardResponse {
  cardId: number;
  name: string;
  cleanName: string;
  imageUrl: string;
  setId: number;
  categoryId: number;
  price: number;
}

interface ListingCardView {
  listingId: number;
  cardId: number;
  name: string;
  imageUrl: string;
  conditionRating: number;
  price?: number;
}

interface UserResponse {
  userId?: number;
  id?: number;
  username?: string;
}

@Component({
  selector: 'app-marketplace-page',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './marketplace-page.component.html',
  styleUrl: './marketplace-page.component.css',
})
export class MarketplacePageComponent implements OnInit {
  isListModalOpen = false;
  isLoading = true;
  loadError = '';
  listings: ListingCardView[] = [];
  listingError = '';
  listingSuccess = '';
  cardSearchTerm = '';
  cardSearchResults: CardResponse[] = [];
  selectedCard: CardResponse | null = null;
  conditionRating = 8;
  private searchTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private http: HttpClient,
    private auth: AuthService,
  ) {}

  ngOnInit(): void {
    this.loadListings();
  }

  openListModal(): void {
    this.isListModalOpen = true;
    this.listingError = '';
    this.listingSuccess = '';
  }

  closeListModal(): void {
    this.isListModalOpen = false;
    this.cardSearchTerm = '';
    this.cardSearchResults = [];
    this.selectedCard = null;
    this.conditionRating = 8;
  }

  onCardSearch(value: string): void {
    this.cardSearchTerm = value;
    this.listingError = '';
    this.listingSuccess = '';

    if (this.searchTimer) {
      clearTimeout(this.searchTimer);
    }

    if (value.trim().length < 2) {
      this.cardSearchResults = [];
      return;
    }

    this.searchTimer = setTimeout(() => {
      this.http
        .get<CardResponse[]>(
          `${environment.apiUrl}/api/cards?name=${encodeURIComponent(value.trim())}`,
          { headers: this.getAuthHeaders() },
        )
        .pipe(catchError(() => of([])))
        .subscribe((results) => {
          this.cardSearchResults = results.slice(0, 8);
        });
    }, 250);
  }

  selectCard(card: CardResponse): void {
    this.selectedCard = card;
    this.cardSearchTerm = card.name;
    this.cardSearchResults = [];
  }

  submitListing(): void {
    if (!this.selectedCard) {
      this.listingError = 'Select a card before publishing.';
      return;
    }

    this.resolveOwnerUserId().subscribe({
      next: (ownerUserId) => {
        const payload = {
          ownerUserId,
          cardId: this.selectedCard!.cardId,
          conditionRating: this.conditionRating,
        };

        this.http
          .post(`${environment.apiUrl}/api/listings`, payload, {
            headers: this.getAuthHeaders(),
          })
          .subscribe({
            next: () => {
              this.listingSuccess = 'Listing published.';
              this.loadListings();
              setTimeout(() => this.closeListModal(), 800);
            },
            error: () => {
              this.listingError = 'Unable to publish listing.';
            },
          });
      },
      error: () => {
        this.listingError = 'Login required to publish a listing.';
      },
    });
  }

  private loadListings(): void {
    this.isLoading = true;
    this.loadError = '';

    this.http
      .get<ListingResponse[]>(`${environment.apiUrl}/api/listings/active`, {
        headers: this.getAuthHeaders(),
      })
      .pipe(
        switchMap((listings) => {
          if (!listings.length) {
            return of({ listings, cards: [] as (CardResponse | null)[] });
          }
          const cardIds = Array.from(
            new Set(listings.map((listing) => listing.cardId)),
          );
          return forkJoin(
            cardIds.map((cardId) =>
              this.http
                .get<CardResponse>(
                  `${environment.apiUrl}/api/cards/${cardId}`,
                  { headers: this.getAuthHeaders() },
                )
                .pipe(catchError(() => of(null))),
            ),
          ).pipe(map((cards) => ({ listings, cards })));
        }),
      )
      .subscribe({
        next: ({ listings, cards }) => {
          const cardMap = new Map<number, CardResponse>();
          cards
            .filter(Boolean)
            .forEach((card) => cardMap.set(card!.cardId, card!));
          this.listings = listings.map((listing) => {
            const card = cardMap.get(listing.cardId);
            return {
              listingId: listing.listingId,
              cardId: listing.cardId,
              name: card?.name ?? 'Unknown card',
              imageUrl: card?.imageUrl ?? '',
              conditionRating: listing.conditionRating,
              price: card?.price,
            };
          });
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
          this.loadError = 'Unable to load listings right now.';
        },
      });
  }

  private resolveOwnerUserId() {
    const username = this.auth.username;
    if (!username) {
      return of(null).pipe(
        switchMap(() => {
          throw new Error('Missing username');
        }),
      );
    }

    return this.http
      .get<UserResponse>(
        `${environment.apiUrl}/api/users/username/${encodeURIComponent(username)}`,
        {
          headers: this.getAuthHeaders(),
        },
      )
      .pipe(
        map((user) => user.userId ?? user.id ?? 0),
        switchMap((id) => {
          if (!id) {
            throw new Error('Missing user id');
          }
          return of(id);
        }),
      );
  }

  private getAuthHeaders(): HttpHeaders {
    const token = this.auth.getToken();
    if (!token) {
      return new HttpHeaders();
    }
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }
}
