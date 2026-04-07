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
    { from: 'bot', text: "Hi! 👋 I'm LogiBot. Ask me about cargo, documents, tracking, or driver updates.", time: new Date() }
  ];

  constructor(private httpService: HttpService) {}

  toggle() {
    console.log('✅ Chatbot clicked');
    this.isOpen = !this.isOpen;
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

    this.httpService.sendChatMessage({ message: msg }).subscribe({
      next: (res: any) => {
        this.messages.push({
          from: 'bot',
          text: res?.reply ?? 'Sorry, I could not respond.',
          time: new Date()
        });
        this.loading = false;
        this.scrollToBottom();
      },
      error: () => {
        this.messages.push({
          from: 'bot',
          text: '⚠️ Chat service error. Please try again.',
          time: new Date()
        });
        this.loading = false;
        this.scrollToBottom();
      }
    });

    this.scrollToBottom();
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
    }, 50);
  }

}