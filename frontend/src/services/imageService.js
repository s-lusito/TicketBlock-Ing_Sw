import axios from '@/plugins/axios';

export default {
    // Carica un'immagine sul server e restituisce l'URL
    uploadImage(file) {
        const formData = new FormData();
        formData.append('file', file);
        return axios.post('/images/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    }
};
