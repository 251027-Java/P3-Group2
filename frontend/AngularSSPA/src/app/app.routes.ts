import { Routes } from '@angular/router';

import { LoginPageComponent } from './pages/login-page/login-page.component';
import { MarketplacePageComponent } from './pages/marketplace-page/marketplace-page.component';
import { SignupPageComponent } from './pages/signup-page/signup-page.component';
import { TradePageComponent } from './pages/trade-page/trade-page.component';
import { EmptyRouteComponent } from './empty-route/empty-route.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginPageComponent },
  { path: 'signup', component: SignupPageComponent },
  { path: 'marketplace', component: MarketplacePageComponent },
  { path: 'trade', component: TradePageComponent },
  { path: '**', component: EmptyRouteComponent }
];
