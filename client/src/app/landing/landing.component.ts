import { Component, ElementRef, OnDestroy, OnInit, AfterViewInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-landing',
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.scss']
})
export class LandingComponent implements OnInit, AfterViewInit, OnDestroy {

  // =========================
  // ✅ TYPEWRITER (Existing)
  // =========================
  private phrases: string[] = [
    'Your Trusted Logistics Wala.',
    'Fast. Secure. Reliable Delivery.',
    'Door-to-door Shipping Across India.',
    'Track Your Cargo in Real Time.'
  ];

  typedText = '';
  private phraseIndex = 0;
  private charIndex = 0;

  private typingSpeed = 65;
  private deletingSpeed = 35;
  private pauseAfterTyping = 1100;
  private pauseAfterDeleting = 350;

  private isDeleting = false;
  private timerId: any;

  // =========================
  // ✅ FACTS COUNTERS (New)
  // =========================
  @ViewChild('factsSection') factsSection!: ElementRef;

  // Target numbers (your current stats)
  private targetClients = 2500;
  private targetOrders = 12000;
  private targetSatisfaction = 98;

  // Display numbers (animated on screen)
  displayClients = 0;
  displayOrders = 0;
  displaySatisfaction = 0;

  private hasAnimatedFacts = false;
  private observer!: IntersectionObserver;

  // Optional: keep increasing counts slowly
  private keepAliveOrdersTimer: any;
  private keepAliveClientsTimer: any;

  // =========================
  // Lifecycle
  // =========================
  ngOnInit(): void {
    this.startTypingLoop();
  }

  ngAfterViewInit(): void {
    // Trigger count-up only when facts section enters viewport
    this.observer = new IntersectionObserver(
      (entries) => {
        const entry = entries[0];
        if (entry.isIntersecting && !this.hasAnimatedFacts) {
          this.hasAnimatedFacts = true;
          this.animateFacts();
          this.observer.disconnect();
        }
      },
      { threshold: 0.35 }
    );

    if (this.factsSection?.nativeElement) {
      this.observer.observe(this.factsSection.nativeElement);
    }
  }

  ngOnDestroy(): void {
    // typewriter timer
    if (this.timerId) clearTimeout(this.timerId);

    // facts timers
    if (this.keepAliveOrdersTimer) clearInterval(this.keepAliveOrdersTimer);
    if (this.keepAliveClientsTimer) clearInterval(this.keepAliveClientsTimer);

    // observer
    if (this.observer) this.observer.disconnect();
  }

  // =========================
  // ✅ TYPEWRITER (Existing)
  // =========================
  private startTypingLoop(): void {
    const currentPhrase = this.phrases[this.phraseIndex];

    if (!this.isDeleting) {
      // Typing
      this.typedText = currentPhrase.substring(0, this.charIndex + 1);
      this.charIndex++;

      if (this.charIndex === currentPhrase.length) {
        // pause after full sentence typed
        this.isDeleting = true;
        this.timerId = setTimeout(() => this.startTypingLoop(), this.pauseAfterTyping);
        return;
      }

      this.timerId = setTimeout(() => this.startTypingLoop(), this.typingSpeed);
    } else {
      // Deleting
      this.typedText = currentPhrase.substring(0, this.charIndex - 1);
      this.charIndex--;

      if (this.charIndex === 0) {
        // move to next phrase
        this.isDeleting = false;
        this.phraseIndex = (this.phraseIndex + 1) % this.phrases.length;
        this.timerId = setTimeout(() => this.startTypingLoop(), this.pauseAfterDeleting);
        return;
      }

      this.timerId = setTimeout(() => this.startTypingLoop(), this.deletingSpeed);
    }
  }

  // =========================
  // ✅ FACTS COUNTERS (New)
  // =========================
  private animateFacts(): void {
    const duration = 1400; // ms

    this.countUp(0, this.targetClients, duration, (val) => this.displayClients = val);
    this.countUp(0, this.targetOrders, duration, (val) => this.displayOrders = val);
    this.countUp(0, this.targetSatisfaction, duration, (val) => this.displaySatisfaction = val);

    // Optional: keep increasing after animation (subtle and brand-like)
    setTimeout(() => {
      this.startKeepAliveIncrements();
    }, duration + 400);
  }

  private countUp(start: number, end: number, duration: number, setValue: (v: number) => void): void {
    const startTime = performance.now();

    const tick = (now: number) => {
      const progress = Math.min((now - startTime) / duration, 1);

      // Smooth easing
      const eased = 1 - Math.pow(1 - progress, 3);

      const value = Math.floor(start + (end - start) * eased);
      setValue(value);

      if (progress < 1) requestAnimationFrame(tick);
    };

    requestAnimationFrame(tick);
  }

  private startKeepAliveIncrements(): void {
    // Orders increase steadily (looks like continuous delivery)
    this.keepAliveOrdersTimer = setInterval(() => {
      this.displayOrders += 1;
    }, 3500); // every 3.5 sec

    // Clients increase slower (more believable)
    this.keepAliveClientsTimer = setInterval(() => {
      this.displayClients += 1;
    }, 25000); // every 25 sec
  }
}
