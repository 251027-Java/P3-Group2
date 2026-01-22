import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

interface TradeOption {
  id: number;
  title: string;
  set: string;
  rarity: string;
}

@Component({
  selector: 'app-trade-page',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './trade-page.component.html',
  styleUrl: './trade-page.component.css'
})
export class TradePageComponent implements OnDestroy {
  selectedCard = 'your card';
  selectedTrades = new Set<string>();
  requestSent = false;
  private redirectTimer: ReturnType<typeof setTimeout> | null = null;

  availableCards: TradeOption[] = [
    { id: 1, title: 'Umbreon VMAX', set: 'Evolving Skies', rarity: 'Alt Art' },
    { id: 2, title: 'Gardevoir EX', set: 'Shining Legends', rarity: 'Ultra Rare' },
    { id: 3, title: 'Blastoise', set: 'Base Set', rarity: 'Holo' },
    { id: 4, title: 'Espeon GX', set: 'Hidden Fates', rarity: 'Shiny' },
    { id: 5, title: 'Dragonite V', set: 'Fusion Strike', rarity: 'Full Art' },
    { id: 6, title: 'Lucario VSTAR', set: 'Crown Zenith', rarity: 'Secret' }
  ];

  constructor(route: ActivatedRoute, private router: Router) {
    const cardParam = route.snapshot.queryParamMap.get('card');
    if (cardParam) {
      this.selectedCard = cardParam;
    }
  }

  toggleTrade(cardTitle: string): void {
    if (this.selectedTrades.has(cardTitle)) {
      this.selectedTrades.delete(cardTitle);
      return;
    }
    this.selectedTrades.add(cardTitle);
  }

  submitTrade(): void {
    if (this.selectedTrades.size === 0) {
      return;
    }
    this.requestSent = true;
    this.redirectTimer = setTimeout(() => {
      this.router.navigate(['/marketplace']);
    }, 1600);
  }

  ngOnDestroy(): void {
    if (this.redirectTimer) {
      clearTimeout(this.redirectTimer);
    }
  }
}
