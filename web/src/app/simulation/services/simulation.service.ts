import { Injectable, NgZone } from '@angular/core'; // Importa Injectable para definir un servicio inyectable y NgZone para ejecutar código dentro de la zona de Angular.
import { BehaviorSubject, map, Observable, switchMap, tap } from 'rxjs'; // Importa operadores y clases de RxJS para trabajar con programación reactiva.
import { HttpClient, HttpParams } from '@angular/common/http'; // Importa HttpClient para realizar peticiones HTTP y HttpParams para gestionar parámetros en las URLs.

import { environment } from '../../../environments/environment'; // Importa las variables de entorno (por ejemplo, la URL base de la API).
import { Simulation } from '../interfaces/simulation.interface'; // Importa la interfaz que define la estructura de una simulación.

@Injectable({
  providedIn: 'root', // Hace que el servicio esté disponible en toda la aplicación.
})
export class SimulationService {
  private URL = environment.URL; // URL base para las peticiones, definida en el entorno.
  // BehaviorSubjects que se usan para notificar a otros componentes cuando se actualizan las simulaciones.
  private _refreshSimulations = new BehaviorSubject<void>(undefined);
  private _refreshSimulation = new BehaviorSubject<void>(undefined);
  public copySimulations: Simulation[] = []; // Copia local de las simulaciones, usada para operaciones de filtrado o restauración.

  constructor(private http: HttpClient, private ngZone: NgZone) {} // Inyecta HttpClient para hacer peticiones y NgZone para actualizar la UI de Angular.

  // Getter para exponer _refreshSimulations como un observable.
  get refreshSimulations() {
    return this._refreshSimulations.asObservable();
  }

  // Getter para exponer _refreshSimulation como un observable.
  get refreshSimulation() {
    return this._refreshSimulation.asObservable();
  }

  // Método para obtener todas las simulaciones desde el backend.
  getSimulations() {
    return this._refreshSimulations.pipe(
      // Cada vez que se emite un valor en _refreshSimulations, se realiza una petición GET.
      switchMap(() => this.http.get<Simulation[]>(`${this.URL}/simulation`)),
      // Convierte el nombre de cada simulación a minúsculas para mantener consistencia.
      map((simulations) =>
        simulations.map((simulation) => ({
          ...simulation,
          name: simulation.name.toLowerCase(),
        }))
      ),
      // Guarda una copia de las simulaciones en la propiedad copySimulations.
      tap((simulations) => (this.copySimulations = [...simulations]))
    );
  }

  // Método para eliminar (matar) una simulación a partir de su id.
  deleteSimulation(id: string) {
    return this.http
      .get(`${this.URL}/simulation/kill/${id}`) // Realiza una petición GET para eliminar la simulación.
      .pipe(
        // Después de la petición, emite un evento para refrescar la lista de simulaciones.
        tap(() => this._refreshSimulations.next())
      );
  }

  // Método para detener una simulación en ejecución.
  stoppedSimution(id: string) {
    return this.http.get(`${this.URL}/simulation/stop/${id}`).pipe(
      tap(() => {
        // Emite eventos para refrescar tanto la lista completa como el estado individual de la simulación.
        this._refreshSimulations.next();
        this._refreshSimulation.next();
      })
    );
  }

  // Método para iniciar una simulación.
  startSimulation(id: string) {
    return this.http.get(`${this.URL}/simulation/start/${id}`).pipe(
      tap(() => {
        // Emite eventos para refrescar la lista de simulaciones y el estado de la simulación iniciada.
        this._refreshSimulations.next();
        this._refreshSimulation.next();
      })
    );
  }

  // Método para obtener el estado actual de una simulación específica.
  getSimulationById(id: string) {
    return this._refreshSimulation.pipe(
      // Cada vez que se emite un evento en _refreshSimulation, se realiza una petición GET para obtener el estado.
      switchMap(() =>
        this.http.get<Simulation>(`${this.URL}/simulation/state/${id}`)
      ),
      // Convierte el nombre de la simulación a minúsculas.
      map((simulation) => ({
        ...simulation,
        name: simulation.name.toLowerCase(),
      }))
    );
  }

  // Método para obtener los logs de una simulación en tiempo real utilizando EventSource.
  getSimulationLogsById(id: string) {
    return new Observable((observer) => {
      // Define parámetros para el EventSource, en este caso, un intervalo de 2.
      const params = new HttpParams().set('interval', 2);
      // Crea una nueva conexión EventSource apuntando al endpoint de logs de la simulación.
      const eventSource = new EventSource(
        `${this.URL}/simulation/logs/${id}?${params.toString()}`
      );

      // Cuando se recibe un mensaje, se procesa el dato y se emite al observable.
      eventSource.onmessage = (event) => {
        // Se utiliza ngZone para asegurarse de que la UI se actualice correctamente.
        this.ngZone.run(() => {
          observer.next(this.parseCustomFormat(event.data));
        });
      };

      // Maneja errores de conexión y los emite al observable.
      eventSource.onerror = (error) => {
        this.ngZone.run(() => {
          observer.error(error);
        });
      };

      // Función de limpieza que se ejecuta cuando se desuscribe del observable.
      return () => {
        eventSource.close(); // Cierra la conexión con el servidor.
      };
    });
  }

  // Método privado para parsear un string con un formato personalizado y convertirlo en un objeto JSON.
  private parseCustomFormat(data: string): any {
    // Intenta parsear el string directamente como JSON.
    try {
      const parsedData = JSON.parse(data); // Si el string es un JSON válido, lo parsea.
      return parsedData; // Devuelve el objeto parseado.
    } catch (error) {
      // Si falla el parseo, se asume que el string tiene un formato personalizado.
      // Remueve los caracteres de llave '{' y '}'.
      data = data.replace(/[{}]/g, '');

      // Separa el string en pares clave-valor usando comas y elimina espacios adicionales.
      const pairs = data.split(',').map((pair) => pair.trim());

      // Construye un string que represente un JSON válido.
      let jsonString = '{';
      pairs.forEach((pair, index) => {
        // Divide cada par en clave y valor usando '=' como separador.
        const [key, value] = pair.split('=').map((item) => item.trim());

        // Agrega la clave en formato JSON (con comillas dobles).
        jsonString += `"${key}":`;

        // Determina el tipo de valor y lo agrega de forma adecuada:
        if (value === 'true' || value === 'false') {
          // Si es un booleano, lo agrega sin comillas.
          jsonString += value;
        } else if (!isNaN(Number(value))) {
          // Si es un número, lo agrega sin comillas.
          jsonString += value;
        } else {
          // De lo contrario, lo trata como cadena y lo encierra entre comillas dobles.
          jsonString += `"${value}"`;
        }

        // Añade una coma entre pares, excepto después del último par.
        if (index < pairs.length - 1) {
          jsonString += ',';
        }
      });
      jsonString += '}';

      // Parsea el string resultante en un objeto JSON y lo devuelve.
      return JSON.parse(jsonString);
    }
  }
}
