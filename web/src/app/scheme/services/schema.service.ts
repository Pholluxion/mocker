import { HttpClient } from '@angular/common/http'; // Importa HttpClient para realizar peticiones HTTP a la API.
import { Injectable } from '@angular/core'; // Importa el decorador Injectable para marcar la clase como inyectable en el sistema de dependencias de Angular.

import {
  BehaviorSubject,
  map,
  Observable,
  switchMap,
  tap,
  throwError,
} from 'rxjs'; // Importa clases y operadores de RxJS para trabajar con programación reactiva.
import * as YAML from 'yaml'; // Importa la librería YAML para parsear archivos en formato YAML.

import { environment } from '../../../environments/environment'; // Importa las variables de entorno, como la URL base de la API.
import { SchemaShort } from '../interfaces/schema-short.interface'; // Importa la interfaz SchemaShort para tipar los datos de esquema.
import { __read } from 'tslib'; // Importa __read de tslib (no utilizado en este código).

@Injectable({
  providedIn: 'root', // Declara que este servicio se inyectará de forma global.
})
export class SchemaService {
  private URL = environment.URL; // URL base de la API, tomada de las variables de entorno.
  public copySchemas: SchemaShort[] = []; // Copia local de los esquemas obtenidos, utilizada para operaciones de filtrado y restauración.
  private _refresh = new BehaviorSubject<void>(undefined); // BehaviorSubject que se utiliza para notificar a los suscriptores cuando se produce un cambio.

  constructor(private http: HttpClient) {} // Inyecta HttpClient para poder realizar peticiones HTTP.

  // Getter para exponer _refresh como un observable, permitiendo a otros componentes reaccionar a cambios.
  get refresh() {
    return this._refresh.asObservable();
  }

  // Método privado que convierte un archivo YAML a un string parseado.
  private convertFile(file: File) {
    const allowedExtensions = ['.yaml', '.yml']; // Extensiones permitidas para los archivos YAML.
    const fileExtension = file.name.split('.').pop()?.toLowerCase(); // Obtiene la extensión del archivo en minúsculas.

    if (!(fileExtension && allowedExtensions.includes(`.${fileExtension}`))) {
      // Si la extensión no es válida, se emite un error.
      return throwError(() => new Error('Formato de archivo no permitido'));
    }

    // Retorna un observable que lee y parsea el archivo YAML.
    return new Observable<string>((observer) => {
      const reader = new FileReader(); // Crea un FileReader para leer el contenido del archivo.
      reader.onload = (e) => {
        try {
          // Parsea el contenido del archivo usando la librería YAML.
          const data = YAML.parse(e.target?.result as string);
          observer.next(data); // Emite el dato parseado.
          observer.complete(); // Completa el observable.
        } catch (error) {
          observer.error(new Error('Error al leer el archivo.')); // Emite un error en caso de fallo al parsear.
        }
      };
      reader.readAsText(file); // Lee el archivo como texto.
    });
  }

  // Método para crear un nuevo esquema a partir de un archivo YAML.
  createSchema(file: File) {
    return this.convertFile(file).pipe(
      switchMap((data) => this.http.post(`${this.URL}/schema`, data)), // Realiza una petición POST enviando los datos parseados.
      tap(() => this._refresh.next()) // Notifica a los suscriptores que se ha actualizado la información.
    );
  }

  // Método para obtener el listado de esquemas (en formato corto) desde la API.
  getSchemas() {
    return this._refresh.pipe(
      switchMap(() => this.http.get<SchemaShort[]>(`${this.URL}/schema/short`)), // Realiza una petición GET cada vez que se emite un valor en _refresh.
      map((schemas) =>
        // Convierte el nombre de cada esquema a minúsculas.
        schemas.map((schema) => ({
          ...schema,
          name: schema.name.toLowerCase(),
        }))
      ),
      tap((schemas) => (this.copySchemas = [...schemas])) // Actualiza la copia local de los esquemas.
    );
  }

  // Método privado que convierte un archivo de plantilla (.txt) a string.
  private convertTemplate(file: File) {
    const allowedExtensions = ['.txt']; // Extensiones permitidas para las plantillas.
    const fileExtension = file.name.split('.').pop()?.toLowerCase(); // Obtiene la extensión del archivo.

    if (!(fileExtension && allowedExtensions.includes(`.${fileExtension}`))) {
      // Si la extensión no es válida, se emite un error.
      return throwError(() => new Error('Formato de archivo no permitido'));
    }

    // Retorna un observable que lee el archivo como texto.
    return new Observable<string>((observer) => {
      const reader = new FileReader(); // Crea un FileReader para leer el archivo.
      reader.onload = (e) => {
        try {
          const data = e.target?.result as string; // Obtiene el contenido del archivo como string.
          observer.next(data); // Emite el contenido.
          observer.complete(); // Completa el observable.
        } catch (error) {
          observer.error(new Error('Error al leer el archivo.')); // Emite un error en caso de fallo.
        }
      };
      reader.readAsText(file); // Lee el archivo como texto.
    });
  }

  // Método para actualizar la plantilla de un esquema dado su id, utilizando un archivo de plantilla.
  createTemplate(file: File, id: string) {
    return this.convertTemplate(file).pipe(
      switchMap((body) => {
        return this.http.put(`${this.URL}/schema/template/${id}`, body); // Realiza una petición PUT enviando el contenido del archivo.
      })
    );
  }

  // Método para iniciar la simulación de un esquema dado su id.
  startSchema(id: string) {
    return this.http
      .get(`${this.URL}/simulation/start/${id}`) // Realiza una petición GET para iniciar la simulación.
      .pipe(tap(() => this._refresh.next())); // Notifica a los suscriptores que se ha actualizado el estado.
  }

  // Método comentado para obtener esquemas mediante un stream de eventos (utilizando EventSource).
  // getSchemasStream() {
  //   return new Observable<SchemaShort[]>((observer) => {
  //     const params = new HttpParams().set('interval', 2);
  //     const eventSource = new EventSource(
  //       `${this.URL}/schema/short/stream?${params.toString()}`
  //     );
  //
  //     eventSource.onmessage = (event) => {
  //       this.ngZone.run(() => {
  //         observer.next(JSON.parse(event.data));
  //         this.copySchemas = JSON.parse(event.data);
  //       });
  //     };
  //
  //     eventSource.onerror = (error) => {
  //       this.ngZone.run(() => {
  //         observer.error(error);
  //       });
  //     };
  //
  //     return () => {
  //       eventSource.close();
  //     };
  //   });
  // }

  // Método para eliminar un esquema dado su id.
  deleteSchema(id: string) {
    return this.http
      .delete(`${this.URL}/schema/${id}`) // Realiza una petición DELETE a la API.
      .pipe(tap(() => this._refresh.next())); // Notifica a los suscriptores que se ha actualizado la información.
  }
}
