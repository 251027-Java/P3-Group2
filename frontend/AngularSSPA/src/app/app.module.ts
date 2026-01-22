import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';

import { AppComponent } from './app.component';
import { EmptyRouteComponent } from './empty-route/empty-route.component';
import { LoginPageComponent } from './pages/login-page/login-page.component';
import { SignupPageComponent } from './pages/signup-page/signup-page.component';
import { MarketplacePageComponent } from './pages/marketplace-page/marketplace-page.component';
import { routes } from './app.routes';

@NgModule({
  declarations: [
    AppComponent,
    EmptyRouteComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    // Import standalone components
    LoginPageComponent,
    SignupPageComponent,
    MarketplacePageComponent
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

