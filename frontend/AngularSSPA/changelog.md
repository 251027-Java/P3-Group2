# AngularSSPA Changelog

All notable changes to the AngularSSPA component are documented in this file.

---

## [2026-01-20] - Docker Build & Single-SPA Fixes

### 🔧 Fixed

#### 1. Dockerfile Path Mismatch

- **File**: `Dockerfile`
- **Change**: Updated COPY path from `dist/AngularSSPA` to `dist/angular-sspa`
- **Reason**: The Angular build outputs to `dist/angular-sspa` (lowercase with hyphen) as defined in `angular.json`, but the Dockerfile was trying to copy from `dist/AngularSSPA` (PascalCase). This case-sensitivity mismatch caused the Docker build to fail.

```diff
-COPY --from=builder /app/dist/AngularSSPA /usr/share/nginx/html
+COPY --from=builder /app/dist/angular-sspa /usr/share/nginx/html
```

---

#### 2. Angular.json Schema Validation Error

- **File**: `angular.json`
- **Change**: Changed deprecated `browser` property to `main` and updated entry point
- **Reason**: Angular 17+ renamed the `browser` property to `main` in the build options schema. Also pointed to the Single-SPA entry point `main.single-spa.ts` instead of `main.ts`.

```diff
-"browser": "src/main.ts",
+"main": "src/main.single-spa.ts",
```

Also removed a duplicate `main` property that was present later in the options block.

---

#### 3. Missing AppModule

- **File**: `src/app/app.module.ts` *(NEW)*
- **Change**: Created new NgModule file
- **Reason**: The `main.single-spa.ts` file imports and bootstraps `AppModule`, but no `app.module.ts` existed. Single-SPA requires NgModule-based architecture for proper lifecycle management.

```typescript
@NgModule({
  declarations: [AppComponent, EmptyRouteComponent],
  imports: [BrowserModule, RouterModule.forRoot(routes)],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

---

#### 4. Import Standalone Page Components

- **File**: `src/app/app.module.ts`
- **Change**: Added standalone page components to imports array
- **Reason**: The project uses standalone page components (LoginPageComponent, SignupPageComponent, MarketplacePageComponent) but the NgModule-based AppComponent couldn't access the RouterModule directives. Standalone components must be imported (not declared) in NgModules.

```typescript
imports: [
  BrowserModule,
  RouterModule.forRoot(routes),
  // Import standalone components
  LoginPageComponent,
  SignupPageComponent,
  MarketplacePageComponent
],
```

---

#### 5. Missing Environment Files

- **Files**: `src/environments/environment.ts`, `src/environments/environment.prod.ts` *(NEW)*
- **Change**: Created environment configuration files
- **Reason**: The `main.single-spa.ts` imports from `./environments/environment` for production mode detection, but these files were missing in the project.

```typescript
// environment.ts (development)
export const environment = {
  production: false
};

// environment.prod.ts (production)
export const environment = {
  production: true
};
```

#### 6. Bootstrap Mode Mismatch (Critical)

- **File**: `src/main.single-spa.ts`
- **Change**: Reverted from standalone `bootstrapApplication()` to NgModule `bootstrapModule(AppModule)`
- **Reason**: A merge changed the bootstrap to use standalone mode, which bypassed the `AppModule` entirely. This meant `RouterModule` was never loaded, causing the `router-outlet` error.

```diff
-import { bootstrapApplication } from '@angular/platform-browser';
-import { AppComponent } from './app/app.component';
-const lifecycles = singleSpaAngular({
-  bootstrapFunction: () => bootstrapApplication(AppComponent, appConfig),
+import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
+import { AppModule } from './app/app.module';
+const lifecycles = singleSpaAngular({
+  bootstrapFunction: singleSpaProps => {
+    return platformBrowserDynamic(getSingleSpaExtraProviders()).bootstrapModule(AppModule);
+  },
```

---

#### 7. Standalone Component Compatibility (Angular 17+)

- **Files**: `src/app/app.component.ts`, `src/app/empty-route/empty-route.component.ts`
- **Change**: Added `standalone: false` to component decorators
- **Reason**: Angular 17+ defaults to `standalone: true` for all components. NgModule-based Single-SPA requires components to be declared in modules, not standalone. Must explicitly set `standalone: false`.

```diff
 @Component({
   selector: 'app-root',
+  standalone: false,
   templateUrl: './app.component.html',
   styleUrl: './app.component.css'
 })
```

```diff
 @Component({
   selector: 'app-empty-route',
+  standalone: false,
   template: '',
 })
```

---

## Summary of Files Modified

| File | Action | Description |
| ---- | ------ | ----------- |
| `Dockerfile` | Modified | Fixed output path case sensitivity, healthcheck port |
| `angular.json` | Modified | Changed `browser` → `main`, removed duplicate |
| `src/app/app.module.ts` | Created | Added NgModule with standalone component imports |
| `src/app/app.component.ts` | Modified | Added `standalone: false` |
| `src/app/empty-route/empty-route.component.ts` | Modified | Added `standalone: false` |
| `src/environments/environment.ts` | Created | Development environment config |
| `src/environments/environment.prod.ts` | Created | Production environment config |

---

## Single-SPA Compatibility Notes

This Angular component is configured to work as a **micro-frontend** in a Single-SPA application:

- **Entry Point**: `src/main.single-spa.ts` - exports `bootstrap`, `mount`, and `unmount` lifecycle functions
- **UMD Bundle**: Built using `@angular-builders/custom-webpack:browser` with UMD library output
- **Zone.js**: Included as a polyfill for Angular change detection
- **Router**: Configured via `RouterModule.forRoot(routes)` in AppModule
