import { Component, OnInit, OnDestroy, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatbotService, ChatMessage } from '../../services/chatbot.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-chatbot',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './chatbot.component.html',
    styleUrls: ['./chatbot.component.css']
})
export class ChatbotComponent implements OnInit, OnDestroy {
    @ViewChild('chatMessages') chatMessagesRef!: ElementRef;

    isOpen = false;
    isMinimized = false;
    messages: ChatMessage[] = [];
    userInput = '';
    isLoading = false;
    private subscription?: Subscription;

    // Quick action suggestions
    quickActions = [
        '🏨 Hotels in Paris',
        '🏖️ Beach resorts in Bali',
        '💰 Budget hotels',
        '⭐ Luxury hotels'
    ];

    constructor(private chatbotService: ChatbotService) { }

    ngOnInit(): void {
        // Welcome message
        this.messages.push({
            role: 'assistant',
            content: 'Hello! 👋 I\'m your hotel recommendation assistant. Ask me about hotels, destinations, or travel tips!',
            timestamp: new Date()
        });
    }

    ngOnDestroy(): void {
        this.subscription?.unsubscribe();
    }

    toggleChat(): void {
        this.isOpen = !this.isOpen;
        if (this.isOpen) {
            this.isMinimized = false;
            setTimeout(() => this.scrollToBottom(), 100);
        }
    }

    toggleMinimize(): void {
        this.isMinimized = !this.isMinimized;
    }

    closeChat(): void {
        this.isOpen = false;
    }

    sendMessage(): void {
        if (!this.userInput.trim() || this.isLoading) return;

        const userMessage = this.userInput.trim();
        this.userInput = '';

        // Add user message
        this.messages.push({
            role: 'user',
            content: userMessage,
            timestamp: new Date()
        });

        this.scrollToBottom();
        this.isLoading = true;

        // Send to chatbot API
        this.subscription = this.chatbotService.sendMessage(userMessage).subscribe({
            next: (response) => {
                this.messages.push({
                    role: 'assistant',
                    content: response.response,
                    hotels: response.hotels,  // Include hotel data
                    timestamp: new Date()
                });
                this.scrollToBottom();
                this.isLoading = false;
            },
            error: (error) => {
                console.error('Chat error:', error);
                this.messages.push({
                    role: 'assistant',
                    content: 'Sorry, I encountered an error. Please try again later.',
                    timestamp: new Date()
                });
                this.scrollToBottom();
                this.isLoading = false;
            }
        });
    }

    useQuickAction(action: string): void {
        this.userInput = action.replace(/[🏨🏖️💰⭐]/g, '').trim();
        this.sendMessage();
    }

    private scrollToBottom(): void {
        setTimeout(() => {
            if (this.chatMessagesRef) {
                const element = this.chatMessagesRef.nativeElement;
                element.scrollTop = element.scrollHeight;
            }
        }, 100);
    }
}
