import { Component } from '@angular/core';
import { HttpService } from '../../services/http.service';

type ChatMsg = { from: 'user' | 'bot'; text: string; time: Date };

@Component({
  selector: 'app-chatbot',
  templateUrl: './chatbot.component.html',
  styleUrls: ['./chatbot.component.scss']
})
export class ChatbotComponent {

  isOpen = false;
  input = '';
  loading = false;

  messages: ChatMsg[] = [
    {
      from: 'bot',
      text: "Hello! I’m CargoWala Assistant. Ask me about cargo creation, documents, tracking, driver updates, or payments.",
      time: new Date()
    }
  ];

  constructor(private httpService: HttpService) {}

  toggle() {
    this.isOpen = !this.isOpen;
    if (this.isOpen) this.scrollToBottom();
  }

  close() {
    this.isOpen = false;
  }

  send() {
    const msg = this.input.trim();
    if (!msg || this.loading) return;

    this.messages.push({ from: 'user', text: msg, time: new Date() });
    this.input = '';
    this.loading = true;
    this.scrollToBottom();

    this.httpService.sendChatMessage({ message: msg }).subscribe({
      next: (res: any) => {
        this.messages.push({
          from: 'bot',
          text: res?.reply ?? 'Sorry, I could not respond right now.',
          time: new Date()
        });
        this.loading = false;
        this.scrollToBottom();
      },
      error: () => {
        this.messages.push({
          from: 'bot',
          text: 'Chat service error. Please try again.',
          time: new Date()
        });
        this.loading = false;
        this.scrollToBottom();
      }
    });
  }

  onEnter(e: KeyboardEvent) {
    if (e.key === 'Enter') {
      e.preventDefault();
      this.send();
    }
  }

  private scrollToBottom() {
    setTimeout(() => {
      const box = document.getElementById('chatScrollBox');
      if (box) box.scrollTop = box.scrollHeight;
    }, 60);
  }
}