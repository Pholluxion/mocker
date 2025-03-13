import { Component, EventEmitter, Input, Output } from '@angular/core'; // Importa los decoradores y clases necesarios de Angular

@Component({
  selector: 'app-shared-button',
  imports: [], // No se especifican módulos adicionales a importar para este componente
  templateUrl: './button.component.html',
  styleUrl: './button.component.scss',
})
export class ButtonComponent {
  @Input() // Permite recibir el título del botón desde un componente padre
  public title = 'Titulo de botón'; // Valor por defecto del título del botón

  @Input() // Permite recibir el tipo de botón (por ejemplo, 'button', 'submit') desde un componente padre
  public type = 'button'; // Valor por defecto del tipo de botón

  @Output() // Define que la propiedad newFile emitirá eventos hacia el componente padre
  public newFile: EventEmitter<File> = new EventEmitter(); // Crea una instancia de EventEmitter para emitir objetos de tipo File

  // Método que se ejecuta cuando se selecciona un archivo en un input (tipo file)
  onFileSelected(event: Event) {
    const inputElement = event.target as HTMLInputElement; // Convierte el objeto de evento en un elemento HTMLInputElement
    if (!(inputElement.files && inputElement.files.length > 0)) return; // Verifica que se haya seleccionado al menos un archivo; si no, sale del método

    const file = inputElement.files[0]; // Obtiene el primer archivo seleccionado
    this.newFile.emit(file); // Emite el archivo seleccionado al componente padre mediante el EventEmitter

    inputElement.value = ''; // Reinicia el valor del input para permitir futuras selecciones del mismo archivo
  }
}
