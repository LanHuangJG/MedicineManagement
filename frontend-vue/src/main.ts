import {createApp} from 'vue'
import './style.css'
import App from './App.vue'
import {createPinia} from "pinia"
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import router from "./router"
import 'mdui/mdui.css'
import 'mdui'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import {setColorScheme} from "mdui"
import "default-passive-events"
import "echarts";
import VueCountdown from '@chenfengyuan/vue-countdown';
const app = createApp(App)
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)
app.use(router)
app.use(ElementPlus)
app.component(VueCountdown.name, VueCountdown);

setColorScheme('#77cd75')
app.mount('#app')
