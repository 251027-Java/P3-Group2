import './polyfills';

import { enableProdMode, NgZone } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { singleSpaAngular } from 'single-spa-angular';

import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';

// Optional: only use this if you actually have src/environments/environment.ts
// import { environment } from './environments/environment';
// if (environment.production) enableProdMode();

const lifecycles = singleSpaAngular({
  bootstrapFunction: () => bootstrapApplication(AppComponent, appConfig),
  template: '<app-root />',
  NgZone,
});

export const bootstrap = lifecycles.bootstrap;
export const mount = lifecycles.mount;
export const unmount = lifecycles.unmount;
