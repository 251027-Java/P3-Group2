import { Component, OnDestroy } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: false,
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnDestroy {
  title = 'AngularSSPA';
  isMarketplace = false;
  hideHeader = false;
  private routeSub: Subscription;

  constructor(private router: Router) {
    this.isMarketplace = this.router.url.startsWith('/marketplace');
    this.hideHeader = this.shouldHideHeader(this.router.url);
    this.routeSub = this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event) => {
        this.isMarketplace = event.urlAfterRedirects.startsWith('/marketplace');
        this.hideHeader = this.shouldHideHeader(event.urlAfterRedirects);
      });
  }

  private shouldHideHeader(url: string): boolean {
    return url.startsWith('/marketplace') || url.startsWith('/login') || url.startsWith('/signup');
  }

  ngOnDestroy(): void {
    this.routeSub.unsubscribe();
  }
}
