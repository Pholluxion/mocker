import { Component, OnDestroy, OnInit } from '@angular/core'; // Importa decoradores e interfaces para definir el componente y gestionar su ciclo de vida.
import { SearchComponent } from '../../../shared/components/search/search.component'; // Importa el componente de búsqueda compartido.
import { TableComponent } from '../../components/table/table.component'; // Importa el componente de tabla para listar simulaciones.
import { SimulationService } from '../../services/simulation.service'; // Importa el servicio que maneja la lógica y peticiones relacionadas con simulaciones.
import { Simulation } from '../../interfaces/simulation.interface'; // Importa la interfaz que define la estructura de una simulación.
import { Subscription } from 'rxjs'; // Importa la clase Subscription de RxJS para gestionar suscripciones a observables.
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner'; // Importa el módulo del spinner para mostrar indicadores de carga.

@Component({
  selector: 'app-home',
  imports: [SearchComponent, TableComponent, MatProgressSpinnerModule], // Importa componentes y módulos que se usarán en la plantilla.
  templateUrl: './home.component.html', 
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit, OnDestroy {
  public simulations: Simulation[] = []; // Array que almacenará la lista de simulaciones.
  private simulationSubscription: Subscription = new Subscription(); // Suscripción para gestionar la petición y evitar fugas de memoria.
  public loading: boolean = true; // Bandera que indica si los datos se están cargando.

  // Constructor que inyecta el servicio SimulationService para interactuar con la API o lógica de simulaciones.
  constructor(private simulationService: SimulationService) {}

  // Método del ciclo de vida que se ejecuta al inicializar el componente.
  ngOnInit(): void {
    // Se suscribe al observable que obtiene las simulaciones y se agrega la suscripción a simulationSubscription.
    this.simulationSubscription.add(
      this.simulationService.getSimulations().subscribe((data) => {
        this.simulations = data; // Asigna los datos recibidos a la propiedad simulations.
        this.loading = false; // Desactiva el indicador de carga una vez que los datos han sido recibidos.
      })
    );
  }

  // Método del ciclo de vida que se ejecuta al destruir el componente.
  ngOnDestroy(): void {
    this.simulationSubscription.unsubscribe(); // Cancela todas las suscripciones para evitar fugas de memoria.
  }

  // Método para filtrar las simulaciones en función de un valor de búsqueda.
  filterSimulations(value: string) {
    if (value.length === 0) {
      // Si no se ingresa ningún valor, se restauran todas las simulaciones almacenadas en copySimulations.
      this.simulations = this.simulationService.copySimulations;
    } else {
      // Filtra las simulaciones que incluyan el valor ingresado en su propiedad 'name'.
      this.simulations = this.simulationService.copySimulations.filter(
        (simulation) => simulation.name.includes(value)
      );
    }
  }
}
