import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
    providedIn: 'root'
})
export class ToastService {

    constructor(private toastr: ToastrService) { }

    private getCommonConfig() {
        return {
            positionClass: 'toast-b',
            toastClass: 'toast-s',
            closeButton: true,
            progressBar: true,
            enableHtml: true
        };
    }

    // Success Messages
    showSuccess(message: string, title?: string, timeOut: number = 4000) {
        this.toastr.success(
            `✅ ${message}`,
            title ? `🎉 ${title}` : '🎉 Succès',
            { ...this.getCommonConfig(), timeOut }
        );
    }

    // Error Messages  
    showError(message: string, title?: string, timeOut: number = 5000) {
        this.toastr.error(
            `❌ ${message}`,
            title ? `🚨 ${title}` : '🚨 Erreur',
            { ...this.getCommonConfig(), timeOut }
        );
    }

    // Warning Messages
    showWarning(message: string, title?: string, timeOut: number = 4500) {
        this.toastr.warning(
            `⚠️ ${message}`,
            title ? `🔔 ${title}` : '🔔 Attention',
            { ...this.getCommonConfig(), timeOut }
        );
    }
    showAccesDenied(message: string, title?: string, timeOut: number = 4500) {
        this.toastr.warning(
            ` ❌${message},please contact the support team`,
            title ? `🔔 ${title}` : '🔔 Attention',
            { ...this.getCommonConfig(), timeOut }
        );
    }
    // Info Messages
    showInfo(message: string, title?: string, timeOut: number = 4000) {
        this.toastr.info(
            `ℹ️ ${message}`,
            title ? `💡 ${title}` : '💡 Information',
            { ...this.getCommonConfig(), timeOut }
        );
    }

    // Specific Hotel Messages
    hotelCreated(hotelName: string) {
        this.showSuccess(`L'hôtel "${hotelName}" a été créé avec succès!`, 'Hôtel Ajouté');
    }

    hotelUpdated(hotelName: string) {
        this.showSuccess(`L'hôtel "${hotelName}" a été mis à jour avec succès!`, 'Hôtel Modifié');
    }

    hotelDeleted(hotelName: string) {
        this.showWarning(`L'hôtel "${hotelName}" a été supprimé.`, 'Hôtel Supprimé');
    }

    validationError(fieldName?: string) {
        const message = fieldName
            ? `field "${fieldName}" required.`
            : 'Veuillez remplir tous les champs obligatoires du formulaire.';
        this.showError(message, 'Erreur de Validation');
    }

    serverError() {
        this.showError('Une erreur serveur s\'est produite. Veuillez réessayer.', 'Erreur Serveur');
    }

    networkError() {
        this.showError('Problème de connexion. Vérifiez votre connexion internet.', 'Erreur Réseau');
    }
}
