import { Component, OnInit, OnDestroy, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';

declare const System: any;

@Component({
  selector: 'app-marketplace-page',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './marketplace-page.component.html',
  styleUrl: './marketplace-page.component.css'
})
export class MarketplacePageComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('reactNavbarContainer', { static: false }) reactNavbarContainer?: ElementRef;
  private navbarMfe: any;
  private mountedParcel: any;

  cards = [
    { id: 1, title: 'Charizard', color: 'linear-gradient(135deg, #ff9a9e 0%, #fecfef 99%, #fecfef 100%)' },
    { id: 2, title: 'Blastoise', color: 'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)' },
    { id: 3, title: 'Venusaur', color: 'linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%)' },
    { id: 4, title: 'Pikachu', color: 'linear-gradient(120deg, #f6d365 0%, #fda085 100%)' },
    { id: 5, title: 'Mewtwo', color: 'linear-gradient(120deg, #d4fc79 0%, #96e6a1 100%)' },
    { id: 6, title: 'Gengar', color: 'linear-gradient(120deg, #89f7fe 0%, #66a6ff 100%)' }
  ];

  async ngOnInit() {
    try {
      // Load the React navbar micro-frontend
      this.navbarMfe = await System.import('@marketplace/mfe-react-navbar');
      console.log('React navbar MFE loaded successfully');
      
      // Mount immediately after loading if view is ready
      setTimeout(() => this.mountNavbar(), 0);
    } catch (error) {
      console.error('Failed to load React navbar MFE:', error);
    }
  }

  async ngAfterViewInit() {
    console.log('ngAfterViewInit called');
    // Try to mount if the MFE is already loaded
    await this.mountNavbar();
  }

  private async mountNavbar() {
    console.log('Attempting to mount navbar');
    console.log('navbarMfe:', this.navbarMfe);
    console.log('reactNavbarContainer:', this.reactNavbarContainer);
    
    if (this.navbarMfe && this.reactNavbarContainer) {
      try {
        const containerElement = this.reactNavbarContainer.nativeElement;
        console.log('Container element:', containerElement);
        
        // Bootstrap the navbar with props
        await this.navbarMfe.bootstrap({
          name: '@marketplace/mfe-react-navbar',
          domElement: containerElement
        });
        console.log('Bootstrap complete');
        
        // Mount the React navbar with props
        await this.navbarMfe.mount({
          name: '@marketplace/mfe-react-navbar',
          domElement: containerElement
        });
        console.log('React navbar mounted successfully');
      } catch (error) {
        console.error('Failed to mount React navbar:', error);
        console.error('Error details:', error);
      }
    } else {
      console.warn('Cannot mount navbar - missing navbarMfe or container');
    }
  }

  async ngOnDestroy() {
    // Unmount the React navbar when component is destroyed
    if (this.navbarMfe && this.reactNavbarContainer) {
      try {
        await this.navbarMfe.unmount(this.reactNavbarContainer.nativeElement);
        console.log('React navbar unmounted');
      } catch (error) {
        console.error('Failed to unmount React navbar:', error);
      }
    }
  }
}
