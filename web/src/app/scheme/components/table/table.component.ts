import { Component, inject, Input } from '@angular/core'; // Importa los decoradores y funciones esenciales de Angular: Component para definir el componente, inject para la inyección de dependencias, y Input para recibir datos desde componentes padres.
import { MatButtonModule } from '@angular/material/button'; // Importa el módulo de botones de Angular Material para usar botones estilizados en la plantilla.
import { MatDialog } from '@angular/material/dialog'; // Importa el servicio MatDialog para abrir diálogos modales.
import { CommonModule } from '@angular/common'; // Importa el módulo común de Angular que proporciona directivas básicas (como ngIf y ngFor).

import { SchemaShort } from '../../interfaces/schema-short.interface'; // Importa la interfaz SchemaShort para tipar los datos de esquema.
import { ContentDialogComponent } from '../content-dialog/content-dialog.component'; // Importa el componente de diálogo que se utiliza para confirmar acciones sobre un esquema.
import { SchemaService } from '../../services/schema.service'; // Importa el servicio encargado de la lógica y operaciones CRUD relacionadas con los esquemas.
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar'; // Importa el servicio y el módulo de Snackbar para mostrar notificaciones emergentes.

@Component({
  selector: 'app-schema-table',
  imports: [MatButtonModule, CommonModule, MatSnackBarModule], // Lista de módulos y componentes que este componente requiere.
  templateUrl: './table.component.html',
  styleUrl: './table.component.scss',
})
export class TableComponent {
  @Input()
  public schemas: SchemaShort[] = []; // Propiedad de entrada que recibe un arreglo de esquemas, tipada según la interfaz SchemaShort.

  readonly dialog = inject(MatDialog); // Inyecta el servicio MatDialog para abrir diálogos modales de forma sencilla.

  // Constructor que inyecta las dependencias necesarias:
  constructor(
    private schemaService: SchemaService, // Para manejar operaciones relacionadas con esquemas.
    private snackBar: MatSnackBar // Para mostrar mensajes y notificaciones al usuario.
  ) {}

  // Método para abrir un diálogo de confirmación al intentar eliminar un esquema.
  openDialog(id: string): void {
    // Abre el diálogo utilizando el componente ContentDialogComponent, pasando el id del esquema a través de la propiedad data.
    const dialogRef = this.dialog.open(ContentDialogComponent, {
      width: '350px',
      data: { id }, // Envía el id del esquema como dato al diálogo.
    });

    // Se suscribe al evento afterClosed para ejecutar acciones tras el cierre del diálogo.
    dialogRef.afterClosed().subscribe((result) => {
      // Si el usuario confirma la acción (result es verdadero), se procede a eliminar el esquema.
      if (result) {
        this.schemaService.deleteSchema(id).subscribe({
          next: () => {

          },
          error: (error) => {
            console.error(error); // Registra en consola cualquier error ocurrido durante la eliminación.
          },
        });
      }
    });
  }

  // Método que maneja la selección de un archivo para subir una plantilla asociada a un esquema.
  onFileSelected(event: Event, id: string) {
    // Convierte el target del evento en un HTMLInputElement para acceder a la propiedad files.
    const inputElement = event.target as HTMLInputElement;

    // Verifica que se haya seleccionado al menos un archivo; de lo contrario, finaliza la ejecución.
    if (!(inputElement.files && inputElement.files.length > 0)) return;

    // Toma el primer archivo seleccionado.
    const file = inputElement.files[0];
    // Llama al servicio para crear una plantilla asociada al esquema, pasando el archivo y el id correspondiente.
    this.schemaService.createTemplate(file, id).subscribe({
      next: () => {
        // Muestra un snackbar de éxito si la plantilla se sube correctamente.
        this.snackBar.open('Template subido correctamente.', 'Cerrar', {
          duration: 3000, // Duración en milisegundos durante la cual se muestra la notificación.
          verticalPosition: 'bottom',
          horizontalPosition: 'center',
          panelClass: ['snackbar-success'],
        });
      },
      error: (error) => {
        // Muestra un snackbar de error si ocurre un problema al subir la plantilla, mostrando el mensaje de error.
        this.snackBar.open(error.error.message, 'Cerrar', {
          duration: 3000,
          verticalPosition: 'bottom',
          horizontalPosition: 'center',
          panelClass: ['snackbar-error'], 
        });
      },
    });

    // Reinicia el valor del input para permitir futuras selecciones del mismo archivo si es necesario.
    inputElement.value = '';
  }

  // Método para iniciar la ejecución de un esquema.
  runningSchema(id: string) {
    // Llama al servicio para iniciar el esquema y se suscribe a la respuesta (sin manejo explícito de la respuesta en este ejemplo).
    this.schemaService.startSchema(id).subscribe();
  }
}
