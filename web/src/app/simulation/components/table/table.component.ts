import { Component, inject, Input } from '@angular/core'; // Importa decoradores y funciones esenciales de Angular para definir componentes, inyectar dependencias y recibir inputs desde componentes padres.
import { Simulation } from '../../interfaces/simulation.interface'; // Importa la interfaz Simulation para tipar los datos de simulación.
import { CommonModule } from '@angular/common'; // Importa CommonModule para poder usar directivas comunes de Angular (ngIf, ngFor, etc.) en la plantilla.
import { SimulationService } from '../../services/simulation.service'; // Importa el servicio que gestiona operaciones relacionadas con simulaciones.
import { MatDialog } from '@angular/material/dialog'; // Importa el servicio MatDialog para abrir diálogos modales.
import { ContentDialogComponent } from '../content-dialog/content-dialog.component'; // Importa el componente de diálogo que se utiliza para confirmar acciones.
import { RouterModule } from '@angular/router'; // Importa RouterModule para habilitar funcionalidades de enrutamiento dentro del componente.

@Component({
  selector: 'app-simulation-table',
  imports: [CommonModule, RouterModule], // Importa módulos necesarios para la funcionalidad de la plantilla (directivas comunes y navegación).
  templateUrl: './table.component.html',
  styleUrl: './table.component.scss', 
})
export class TableComponent {
  @Input() // Permite que el componente reciba datos de simulaciones desde su componente padre.
  public simulations: Simulation[] = []; // Almacena un arreglo de simulaciones que se mostrarán en la tabla.

  readonly dialog = inject(MatDialog); // Inyecta el servicio MatDialog para gestionar diálogos modales sin necesidad de usar el constructor.

  // Inyecta el servicio SimulationService a través del constructor para interactuar con la API o lógica de simulaciones.
  constructor(private simulationService: SimulationService) {}

  // Método para abrir un diálogo de confirmación para eliminar una simulación.
  openDialog(id: string): void {
    // Abre el diálogo utilizando ContentDialogComponent, pasando el id de la simulación y estableciendo un ancho fijo.
    const dialogRef = this.dialog.open(ContentDialogComponent, {
      width: '350px',
      data: { id },
    });

    // Se suscribe al cierre del diálogo para saber si se confirmó la acción.
    dialogRef.afterClosed().subscribe((result) => {
      // Si el usuario confirma (result es verdadero), se llama al servicio para eliminar la simulación.
      if (result) {
        this.simulationService.deleteSimulation(id).subscribe({
          next: () => {
            // Aquí se podría agregar lógica adicional tras la eliminación exitosa.
          },
          error: (error) => {
            console.error(error); // Registra cualquier error en la consola.
          },
        });
      }
    });
  }

  // Método para detener una simulación; llama al servicio para detener la simulación según su id.
  stoppedSimulation(id: string) {
    this.simulationService.stoppedSimution(id).subscribe();
  }

  // Método para iniciar una simulación; llama al servicio para iniciar la simulación según su id.
  startSimulation(id: string) {
    this.simulationService.startSimulation(id).subscribe();
  }
}
