import Swal from 'sweetalert2/dist/sweetalert2.js';
class VueSweetalert2 {
    static install(vue, options = {}) {
        var _a;
        const swalLocalInstance = Swal.mixin(options);
        const swalFunction = function (...args) {
            return swalLocalInstance.fire.call(swalLocalInstance, ...args);
        };
        Object.assign(swalFunction, Swal);
        Object.keys(Swal)
            //@ts-ignore
            .filter(key => typeof Swal[key] === 'function')
            .forEach(methodName => {
            //@ts-ignore
            swalFunction[methodName] = swalLocalInstance[methodName].bind(swalLocalInstance);
        });
        // add the instance method
        if (((_a = vue.config) === null || _a === void 0 ? void 0 : _a.globalProperties) && !vue.config.globalProperties.$swal) {
            // vue 3
            vue.config.globalProperties.$swal = swalFunction;
            vue.provide('$swal', swalFunction);
        }
        else if (!Object.prototype.hasOwnProperty.call(vue, '$swal')) {
            // vue 2
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            //@ts-ignore
            vue.prototype.$swal = swalFunction;
            vue['swal'] = swalFunction;
        }
    }
}
export default VueSweetalert2;
//# sourceMappingURL=index.js.map