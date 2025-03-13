import { Component, inject } from '@angular/core'; // Importa el decorador Component para definir un componente y la función inject para inyección de dependencias.
import { MatButtonModule } from '@angular/material/button'; // Importa el módulo de botones de Angular Material para utilizar sus componentes de botón.

import {
  MatDialogActions, // Importa el contenedor de acciones para diálogos, usado para agrupar botones u otras acciones.
  MatDialogClose, // Importa la directiva que permite cerrar un diálogo.
  MatDialogContent, // Importa el contenedor de contenido para diálogos, donde se coloca la información principal.
  MatDialogRef, // Importa la referencia al diálogo, utilizada para interactuar con el mismo (cerrarlo, obtener datos, etc.).
  MatDialogTitle, // Importa el contenedor para el título del diálogo.
} from '@angular/material/dialog';

@Component({
  selector: 'app-shared-content-dialog',
  imports: [
    MatButtonModule, // Permite el uso de componentes de botón de Angular Material dentro de la plantilla.
    MatDialogActions, // Permite utilizar el contenedor de acciones en la plantilla del diálogo.
    MatDialogClose, // Permite utilizar la directiva para cerrar el diálogo desde la plantilla.
    MatDialogTitle, // Permite utilizar el contenedor de título en la plantilla del diálogo.
    MatDialogContent, // Permite utilizar el contenedor de contenido en la plantilla del diálogo.
  ],
  templateUrl: './content-dialog.component.html',
  styleUrl: './content-dialog.component.scss', 
})
export class ContentDialogComponent {
  readonly dialogRef = inject(MatDialogRef<ContentDialogComponent>); // Inyecta la referencia al diálogo, tipada para este componente, permitiendo interactuar con él (cerrarlo, etc.).
}
