import { Component } from '@angular/core';

interface Faq {
  question: string;
  answer: string;
  open?: boolean;
}

@Component({
  selector: 'app-faq',
  templateUrl: './faq.component.html',
  styleUrls: ['./faq.component.scss']
})
export class FaqComponent {

  faqs: Faq[] = [
    {
      question: 'What is CargoWala?',
      answer: 'CargoWala is a logistics management platform that helps businesses create cargos, assign drivers, track shipments in real time, and manage payments efficiently.'
    },
    {
      question: 'How do I create a cargo?',
      answer: 'Log in as a Business user, go to the “Create Cargo” section, fill in cargo details, upload documents if required, and submit.'
    },
    {
      question: 'When can I assign a driver to a cargo?',
      answer: 'Drivers can be assigned only when the cargo status is “Order Pending”. Once assigned, the status updates automatically.'
    },
    {
      question: 'When is payment allowed for a cargo?',
      answer: 'Payments can be done only after a driver is assigned. You may choose to pay immediately or later before or after delivery.'
    },
    {
      question: 'How can customers track their shipment?',
      answer: 'Customers can use the “Track” section by entering the cargo A.W.B number to view live status and shipment progress.'
    },
    {
      question: 'What documents can be uploaded?',
      answer: 'You can upload invoices, delivery notes, permits, or any supporting documents in PDF, JPG, PNG, or DOC formats.'
    },
    {
      question: 'What if I face a payment or assignment issue?',
      answer: 'If any issue occurs, please check the cargo status first. For unresolved issues, contact support through the Contact page or via the chatbot.'
    },
    {
      question: 'Is CargoWala limited to a specific region?',
      answer: 'No. CargoWala is designed to support logistics operations globally and is not limited to any one country.'
    }
  ];

  toggle(faq: Faq): void {
    faq.open = !faq.open;
  }
}