import './assets/main.css'

import {createApp} from 'vue'
import App from './App.vue'
import appRouter from "@/appRouter.js";
import i18n from "@/i18n.js";


const app = createApp(App);
app.use(appRouter);
app.use(i18n);
app.mount('#app');
