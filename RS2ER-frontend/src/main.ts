import './plugins/axios'
import Vue, { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import installElementPlus from './plugins/element'

import VueFusionCharts from 'vue-fusioncharts';
import FusionCharts from 'fusioncharts';
import Column2D from 'fusioncharts/fusioncharts.charts';
import * as PowerCharts from 'fusioncharts/fusioncharts.powercharts';
import FusionTheme from 'fusioncharts/themes/fusioncharts.theme.fusion';

const app = createApp(App)
app.use(installElementPlus)
app.use(VueFusionCharts, FusionCharts, Column2D, FusionTheme, PowerCharts);
app.use(store).use(router).mount('#app')
app.config.warnHandler = () => null