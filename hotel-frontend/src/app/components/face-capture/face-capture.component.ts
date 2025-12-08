import { Component, ElementRef, OnDestroy, OnInit, ViewChild, Output, EventEmitter, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-face-capture',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './face-capture.component.html',
  styleUrl: './face-capture.component.css'
})
export class FaceCaptureComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('videoElement', { static: false }) videoElement!: ElementRef<HTMLVideoElement>;
  @ViewChild('canvasElement', { static: false }) canvasElement!: ElementRef<HTMLCanvasElement>;

  @Output() imageCaptured = new EventEmitter<Blob>();
  @Output() captureCancelled = new EventEmitter<void>();

  private mediaStream: MediaStream | null = null;

  isStreaming = false;
  errorMessage = '';
  capturedImage: string | null = null;
  isCameraReady = false;

  ngOnInit(): void {
    // Don't start camera here, wait for view
  }

  ngAfterViewInit(): void {
    // Start camera after view is fully initialized
    setTimeout(() => this.startCamera(), 100);
  }

  ngOnDestroy(): void {
    this.stopCamera();
  }

  async startCamera(): Promise<void> {
    try {
      this.errorMessage = '';
      console.log('Starting camera...');

      // Check if mediaDevices is supported
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        throw new Error('Camera API not supported in this browser');
      }

      // Request camera access
      this.mediaStream = await navigator.mediaDevices.getUserMedia({
        video: {
          width: { ideal: 640 },
          height: { ideal: 480 },
          facingMode: 'user'
        },
        audio: false
      });

      console.log('Camera access granted, stream obtained');

      this.isStreaming = true;

      // Attach stream to video element
      if (this.videoElement?.nativeElement) {
        const video = this.videoElement.nativeElement;
        console.log('Attaching stream to video element');

        video.srcObject = this.mediaStream;
        video.muted = true;
        video.playsInline = true;

        // Wait for video to be ready
        await new Promise<void>((resolve) => {
          video.onloadedmetadata = () => {
            console.log('Video metadata loaded');
            resolve();
          };
        });

        await video.play();
        console.log('Video playing successfully');
        this.isCameraReady = true;
      } else {
        console.error('Video element not available');
      }
    } catch (error: any) {
      console.error('Error accessing camera:', error);

      // Detailed error messages
      if (error.name === 'NotAllowedError' || error.name === 'PermissionDeniedError') {
        this.errorMessage = 'Camera access denied. Please allow camera permission in your browser settings.';
      } else if (error.name === 'NotFoundError' || error.name === 'DevicesNotFoundError') {
        this.errorMessage = 'No camera found. Please connect a camera and try again.';
      } else if (error.name === 'NotReadableError' || error.name === 'TrackStartError') {
        this.errorMessage = 'Camera is already in use by another application. Please close other apps using the camera.';
      } else if (error.name === 'OverconstrainedError') {
        this.errorMessage = 'Camera does not support the requested settings.';
      } else {
        this.errorMessage = 'Unable to access camera: ' + (error.message || 'Unknown error');
      }

      this.isStreaming = false;
      this.isCameraReady = false;
    }
  }

  captureImage(): void {
    if (!this.videoElement || !this.canvasElement) {
      return;
    }

    const video = this.videoElement.nativeElement;
    const canvas = this.canvasElement.nativeElement;
    const context = canvas.getContext('2d');

    if (!context) {
      this.errorMessage = 'Unable to get canvas context';
      return;
    }

    // Set canvas dimensions to match video
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;

    // Draw video frame to canvas
    context.drawImage(video, 0, 0, canvas.width, canvas.height);

    // Get image as data URL for preview
    this.capturedImage = canvas.toDataURL('image/jpeg', 0.9);

    // Stop camera
    this.stopCamera();
  }

  retakeImage(): void {
    this.capturedImage = null;
    this.isStreaming = true;
    this.isCameraReady = true;
    // Wait for Angular to re-render the video element
    setTimeout(() => this.startCamera(), 100);
  }

  confirmImage(): void {
    if (!this.canvasElement) {
      return;
    }

    const canvas = this.canvasElement.nativeElement;

    // Convert canvas to blob
    canvas.toBlob((blob) => {
      if (blob) {
        this.imageCaptured.emit(blob);
      }
    }, 'image/jpeg', 0.9);
  }

  cancel(): void {
    this.stopCamera();
    this.captureCancelled.emit();
  }

  private stopCamera(): void {
    if (this.mediaStream) {
      this.mediaStream.getTracks().forEach(track => track.stop());
      this.mediaStream = null;
    }
    this.isStreaming = false;
    this.isCameraReady = false;
  }
}
