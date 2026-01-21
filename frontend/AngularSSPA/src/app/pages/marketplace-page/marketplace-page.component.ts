import { Component } from '@angular/core';

@Component({
  selector: 'app-marketplace-page',
  standalone: true,
  templateUrl: './marketplace-page.component.html',
  styleUrl: './marketplace-page.component.css'
})
export class MarketplacePageComponent {
  cards = [
    { id: 1, title: 'Charizard', color: 'linear-gradient(135deg, #ff9a9e 0%, #fecfef 99%, #fecfef 100%)' },
    { id: 2, title: 'Blastoise', color: 'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)' },
    { id: 3, title: 'Venusaur', color: 'linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%)' },
    { id: 4, title: 'Pikachu', color: 'linear-gradient(120deg, #f6d365 0%, #fda085 100%)' },
    { id: 5, title: 'Mewtwo', color: 'linear-gradient(120deg, #d4fc79 0%, #96e6a1 100%)' },
    { id: 6, title: 'Gengar', color: 'linear-gradient(120deg, #89f7fe 0%, #66a6ff 100%)' }
  ];
}
