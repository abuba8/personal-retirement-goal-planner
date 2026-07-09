import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-delete-confirmation-modal',
  imports: [],
  templateUrl: './delete-confirmation-modal.html',
  styleUrl: './delete-confirmation-modal.css',
})
export class DeleteConfirmationModal {
  visible = input.required<boolean>();
  recordName = input.required<string>();
  confirmed = output<void>();
  cancelled = output<void>();
}
