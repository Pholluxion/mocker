import { Component, inject, OnDestroy, OnInit } from '@angular/core'; // Importa los decoradores y las interfaces necesarias para crear un componente Angular y gestionar sus ciclos de vida.
import { MatDialog } from '@angular/material/dialog'; // Importa el servicio de diálogo de Angular Material para abrir ventanas modales.
import { CommonModule } from '@angular/common'; // Importa el módulo común de Angular que incluye directivas y pipes básicos.
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner'; // Importa el módulo de spinners para mostrar indicadores de carga.
import { ActivatedRoute, Router } from '@angular/router'; // Importa servicios para acceder a la ruta activa y para navegar entre rutas.

import { Subscription, switchMap, takeWhile } from 'rxjs'; // Importa herramientas y operadores de RxJS para trabajar con observables.

import { ConsoleComponent } from '../../components/console/console.component'; // Importa el componente de los logs.
import { SimulationService } from '../../../simulation/services/simulation.service'; // Importa el servicio que maneja la lógica y la comunicación con simulaciones.
import { Simulation } from '../../../simulation/interfaces/simulation.interface'; // Importa la interfaz que define la estructura de una simulación.
import { ContentDialogComponent } from '../../../simulation/components/content-dialog/content-dialog.component'; // Importa el componte del diálogo de contenido.

@Component({
  selector: 'app-home-page',
  imports: [CommonModule, MatProgressSpinnerModule, ConsoleComponent], // Módulos y componentes que se importan para ser usados internamente en este componente.
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.scss',
})
export class HomePageComponent implements OnInit, OnDestroy {
  public simulation?: Simulation; // Propiedad que almacenará los datos de la simulación actual; puede ser undefined si no se encuentra.
  readonly dialog = inject(MatDialog); // Inyección del servicio MatDialog para abrir diálogos, usando la función inject de Angular.
  private logsSubscription: Subscription = new Subscription(); // Suscripción que agrupa las suscripciones a los logs, para poder desuscribirlas en OnDestroy.
  public dataLogs: any[] = []; // Array que almacenará los datos de logs que se mostrarán en el componente.
  public loading: boolean = true; // Indica el estado de carga; inicia en true para mostrar un spinner de carga.

  // Constructor que inyecta las dependencias necesarias:
  constructor(
    private activatedRoute: ActivatedRoute, // Para acceder a los parámetros de la ruta,
    private simulationService: SimulationService, // Para obtener datos y acciones relacionadas con simulaciones,
    private router: Router // Para realizar navegaciones dentro de la aplicación.
  ) {}

  // Método del ciclo de vida que se ejecuta al inicializar el componente.
  ngOnInit(): void {
    // Se suscribe a los parámetros de la ruta y, usando switchMap, se realiza una petición para obtener la simulación por su id.
    this.activatedRoute.params
      .pipe(switchMap(({ id }) => this.simulationService.getSimulationById(id)))
      .subscribe({
        next: (simulation) => {
          // Si no se encuentra una simulación, se redirige al listado de simulaciones.
          if (!simulation) {
            this.router.navigateByUrl('simulations');
          }
          // Se almacena la simulación obtenida en la propiedad local.
          this.simulation = simulation;
          // Si la simulación aún no ha sido detenida, se inicia la suscripción para obtener logs en tiempo real.
          if (simulation.state !== 'STOPPED') {
            this.subscribeToLogs(simulation.id);
          }
        },
        error: () => {
          // En caso de error al obtener la simulación, se redirige al listado de simulaciones.
          this.router.navigateByUrl('simulations');
        },
      });
  }
  // Método privado para suscribirse a los logs de la simulación utilizando su id.
  private subscribeToLogs(id: string): void {
    this.logsSubscription.add(
      this.simulationService
        .getSimulationLogsById(id)
        .pipe(takeWhile(() => this.simulation?.state !== 'STOPPED')) // Continúa la suscripción mientras la simulación no esté detenida.
        .subscribe({
          next: (data) => {
            this.dataLogs.push(data); // Agrega cada nuevo dato de log al arreglo dataLogs.
            // console.log(this.dataLogs);
            this.loading = false; // Agrega cada nuevo dato de log al arreglo dataLogs.
          },
          // Registra cualquier error ocurrido durante la obtención de logs.
          error: (error) => {
            console.error('Error fetching logs:', error);
            // this.loading = false;
          },
          complete: () => {
            // console.log('STOPPED de simulacion');
            // this.logsSubscription.unsubscribe();
          },
        })
    );
  }
  // Método del ciclo de vida que se ejecuta al destruir el componente.
  ngOnDestroy(): void {
    // console.log('STOP del subscribe de getSimulationLogsById');
    // Se desuscribe de todas las suscripciones para evitar fugas de memoria.
    this.logsSubscription.unsubscribe();
  }
  // Método para abrir un diálogo de confirmación antes de eliminar una simulación.
  openDialog(id: string): void {
    // Abre un diálogo modal utilizando el ContentDialogComponent, pasando el id de la simulación en los datos.
    const dialogRef = this.dialog.open(ContentDialogComponent, {
      width: '350px',
      data: { id },
    });
    // Se suscribe al cierre del diálogo para verificar la respuesta del usuario.
    dialogRef.afterClosed().subscribe((result) => {
      // Si el usuario confirma la acción (result es verdadero), se procede a eliminar la simulación.
      if (result) {
        this.simulationService.deleteSimulation(id).subscribe({
          next: () => {
            // Tras eliminar la simulación, se redirige al listado de simulaciones.
            this.router.navigateByUrl('simulations');
          },
          error: (error) => {
            // Registra en consola cualquier error ocurrido durante la eliminación.
            console.error(error);
          },
        });
      }
    });
  }
  // Método para detener una simulación activa, invocando el servicio correspondiente.
  stoppedSimulation(id: string) {
    this.simulationService.stoppedSimution(id).subscribe();
  }
  // Método para iniciar una simulación, invocando el servicio correspondiente.
  startSimulation(id: string) {
    this.simulationService.startSimulation(id).subscribe();
  }
}
