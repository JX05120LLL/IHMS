import { SweetAlertOptions } from 'sweetalert2';
import Swal from 'sweetalert2/dist/sweetalert2.js';
type TVueSwalInstance = typeof Swal & typeof Swal.fire;
declare module 'vue/types/vue' {
    interface Vue {
        $swal: TVueSwalInstance;
    }
    interface VueConstructor {
        swal: TVueSwalInstance;
    }
}
declare class VueSweetalert2 {
    static install(vue: any, options?: SweetAlertOptions): void;
}
export default VueSweetalert2;
