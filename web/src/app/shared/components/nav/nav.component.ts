import { Component } from '@angular/core'; // Importa el decorador Component de Angular para definir un componente.
import { RouterModule } from '@angular/router'; // Importa RouterModule para utilizar funcionalidades de enrutamiento en la plantilla.

@Component({
  selector: 'app-shared-nav',
  imports: [RouterModule], // Importa el RouterModule para que el componente pueda usar directivas de enrutamiento, como routerLink.
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.scss',
})
export class NavComponent {} // Define la clase del componente.
