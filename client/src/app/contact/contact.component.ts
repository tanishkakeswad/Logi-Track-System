import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.scss']
})
export class ContactComponent implements OnInit {

  ticketForm!: FormGroup;

  submitted = false;

  // Optional attachment (cannot auto-attach via mailto, only show filename)
  selectedFile: File | null = null;

  // Support info (display only)
  supportEmail = 'support@cargowala.com';
  supportPhone = '+91 98765 43210';
  supportAddressLine1 = 'CargoWala Support Center';
  supportAddressLine2 = 'Global Operations';

  // Ticket target email
  private ticketToEmail = 'prkorade@gmail.com';

  subjects: string[] = [
    'Shipment Delay',
    'Wrong Status / Tracking Issue',
    'Damaged Package',
    'Billing / Payment Issue',
    'Pickup / Delivery Support',
    'Account / Login Issue',
    'Other'
  ];

  priorities: string[] = ['Low', 'Normal', 'High'];

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.ticketForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{10,15}$')]],

      // AWB optional; numeric up to 20 digits
      awb: ['', [Validators.pattern('^[0-9]{0,20}$')]],

      subject: ['', [Validators.required]],
      otherSubject: [''], // required only if subject = Other

      priority: ['Normal', [Validators.required]],
      description: ['', [Validators.required, Validators.minLength(10)]]
    });

    // Conditionally require otherSubject when subject is "Other"
    this.ticketForm.get('subject')?.valueChanges.subscribe((val: string) => {
      const otherCtrl = this.ticketForm.get('otherSubject');
      if (val === 'Other') {
        otherCtrl?.setValidators([Validators.required, Validators.minLength(3)]);
      } else {
        otherCtrl?.clearValidators();
        otherCtrl?.setValue('');
      }
      otherCtrl?.updateValueAndValidity();
    });
  }

  get f() {
    return this.ticketForm.controls;
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files && input.files.length ? input.files[0] : null;

    if (!file) {
      this.selectedFile = null;
      return;
    }

    // Optional validation (same you requested)
    const allowed = ['application/pdf', 'image/jpeg', 'image/png'];
    if (!allowed.includes(file.type)) {
      this.selectedFile = null;
      input.value = '';
      alert('Only PDF, JPG, PNG files are allowed.');
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      this.selectedFile = null;
      input.value = '';
      alert('File size must be under 5 MB.');
      return;
    }

    this.selectedFile = file;
  }

  submitTicket(): void {
    this.submitted = true;

    if (this.ticketForm.invalid) return;

    const subjectValue =
      this.ticketForm.value.subject === 'Other'
        ? this.ticketForm.value.otherSubject
        : this.ticketForm.value.subject;

    const body =
`CargoWala Support Ticket

Name: ${this.ticketForm.value.name}
Email: ${this.ticketForm.value.email}
Phone: ${this.ticketForm.value.phone}
Priority: ${this.ticketForm.value.priority}
Cargo A.W.B No.: ${this.ticketForm.value.awb ? this.ticketForm.value.awb : 'N/A'}

Subject: ${subjectValue}

Description:
${this.ticketForm.value.description}

Attachment (manual): ${this.selectedFile ? this.selectedFile.name : 'None'}
Note: Attachments cannot be auto-added via this form. Please attach manually if needed.
`;

    const mailSubject = `CargoWala Ticket: ${subjectValue}`;
    const mailto = `mailto:${this.ticketToEmail}?subject=${encodeURIComponent(mailSubject)}&body=${encodeURIComponent(body)}`;

    // Open email client with pre-filled content
    window.location.href = mailto;

    // Optional: reset UI after opening mail client
    // this.ticketForm.reset({ priority: 'Normal' });
    // this.selectedFile = null;
    // this.submitted = false;
  }
}