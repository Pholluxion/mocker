import { Routes } from '@angular/router'; // Importa el tipo Routes para definir la configuración de rutas en Angular.

export const routes: Routes = [
  // Ruta por defecto: redirige la ruta vacía ('') a 'schemas'
  { path: '', redirectTo: 'schemas', pathMatch: 'full' },

  // Ruta para la sección de "schemas"
  {
    path: 'schemas',
    loadComponent: () =>
      // Carga el componente HomeComponent de forma perezosa (lazy loading) cuando se accede a la ruta 'schemas'
      import('./scheme/pages/home/home.component').then((m) => m.HomeComponent),
  },

  // Ruta para la sección de "simulations"
  {
    path: 'simulations',
    loadComponent: () =>
      // Carga el componente HomeComponent de forma perezosa para la ruta 'simulations'
      import('./simulation/pages/home/home.component').then((m) => m.HomeComponent),
  },

  // Ruta para acceder a una simulación específica utilizando un parámetro de ruta (id)
  {
    path: 'simulation/:id',
    loadComponent: () =>
      // Carga el componente HomePageComponent de forma perezosa para la ruta 'simulation/:id'
      import('./log/pages/home-page/home-page.component').then((m) => m.HomePageComponent),
  },

  // Ruta comodín: cualquier ruta no definida redirige a 'schemas'
  {
    path: '**',
    redirectTo: 'schemas',
  },
];
