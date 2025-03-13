import { Component, OnDestroy, OnInit } from '@angular/core'; // Importa decoradores e interfaces para definir un componente y manejar su ciclo de vida.
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar'; // Importa el servicio y módulo de SnackBar para mostrar notificaciones al usuario.
import { RouterModule } from '@angular/router'; // Importa el módulo de enrutamiento para manejar la navegación entre vistas.
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner'; // Importa el módulo de spinner para indicar visualmente estados de carga.
import { CommonModule } from '@angular/common'; // Importa directivas comunes de Angular, como *ngIf y *ngFor.

import { Subscription } from 'rxjs'; // Importa la clase Subscription de RxJS para gestionar y cancelar suscripciones.

import { ButtonComponent } from '../../../shared/components/button/button.component'; // Importa un componente personalizado de botón.
import { SearchComponent } from '../../../shared/components/search/search.component'; // Importa un componente personalizado de búsqueda.
import { TableComponent } from '../../components/table/table.component'; // Importa un componente de tabla para listar esquemas.
import { SchemaService } from '../../services/schema.service'; // Importa el servicio encargado de las operaciones relacionadas con esquemas.
import { SchemaShort } from '../../interfaces/schema-short.interface'; // Importa la interfaz que define la estructura resumida de un esquema.

@Component({
  selector: 'app-home',
  imports: [
    RouterModule,           // Permite el uso de funcionalidades de enrutamiento.
    ButtonComponent,        // Hace disponible el componente de botón en la vista.
    SearchComponent,        // Hace disponible el componente de búsqueda en la vista.
    TableComponent,         // Hace disponible el componente de tabla para mostrar esquemas.
    MatSnackBarModule,      // Hace disponible el módulo de SnackBar para notificaciones.
    CommonModule,           // Permite el uso de directivas comunes de Angular.
    MatProgressSpinnerModule, // Permite el uso de spinners para indicar carga.
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit, OnDestroy {
  public schemas: SchemaShort[] = []; // Almacena el listado de esquemas que se mostrarán en la vista.
  private schemasSubcription: Subscription = new Subscription(); // Controla las suscripciones a observables para poder cancelarlas al destruir el componente.
  public loading: boolean = true; // Indica si los datos se están cargando; controla la visualización del spinner de carga.

  // Constructor: inyecta las dependencias necesarias para el funcionamiento del componente.
  constructor(
    private schemaService: SchemaService, // Servicio para obtener, crear y filtrar esquemas.
    private snackBar: MatSnackBar           // Servicio para mostrar notificaciones emergentes (SnackBar).
  ) {}

  // Método del ciclo de vida que se ejecuta al inicializar el componente.
  ngOnInit(): void {
    // Se suscribe al observable que retorna la lista de esquemas.
    this.schemasSubcription.add(
      this.schemaService.getSchemas().subscribe((data) => {
        this.schemas = data; // Asigna los datos recibidos a la propiedad 'schemas'.
        this.loading = false; // Finaliza el estado de carga una vez recibidos los datos.
      })
    );
  }

  // Método del ciclo de vida que se ejecuta al destruir el componente.
  ngOnDestroy(): void {
    this.schemasSubcription.unsubscribe(); // Cancela la suscripción para evitar fugas de memoria.
  }

  // Método para crear un nuevo esquema a partir de un archivo.
  createSchema(file: File) {
    // Llama al servicio para crear el esquema y se suscribe a la respuesta.
    this.schemaService.createSchema(file).subscribe({
      next: () => {
        // Muestra un SnackBar de éxito en caso de que la creación se realice correctamente.
        this.snackBar.open('Esquema creado correctamente.', 'Cerrar', {
          duration: 3000,
          verticalPosition: 'bottom',
          horizontalPosition: 'center',
          panelClass: ['snackbar-success'],
        });
      },
      error: (error) => {
        // Muestra un SnackBar de error con el mensaje recibido en caso de fallo.
        this.snackBar.open(error.error.message, 'Cerrar', {
          duration: 3000,
          verticalPosition: 'bottom',
          horizontalPosition: 'center',
          panelClass: ['snackbar-error'], 
        });
      },
    });
  }

  // Método para filtrar la lista de esquemas según el valor ingresado en la búsqueda.
  filterSchemas(value: string) {
    if (value.length === 0) {
      // Si el valor está vacío, se restaura la lista completa de esquemas.
      this.schemas = this.schemaService.copySchemas;
    } else {
      // Si hay un valor, se filtra la lista comparando si el nombre del esquema lo incluye.
      this.schemas = this.schemaService.copySchemas.filter((schema) =>
        schema.name.includes(value)
      );
    }
  }
}
