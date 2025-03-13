import { bootstrapApplication } from '@angular/platform-browser';// Importa la función 'bootstrapApplication' desde '@angular/platform-browser'
import { appConfig } from './app/app.config'; // Importa la configuración de la aplicación definida en el archivo 'app.config'.
import { AppComponent } from './app/app.component'; // Importa el componente principal (root) de la aplicación.
// Inicia la aplicación Angular utilizando el componente principal (AppComponent)
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));   // Captura y muestra en consola cualquier error que ocurra durante el proceso de inicialización.

