import { createRouter, createWebHistory } from 'vue-router'
import Database from '../views/Database.vue'

const routes = [
  {
    path: '/',
    name: 'database',
    component: Database
  },
  {
    path: '/home',
    name: 'home',
    component: () => import(/* webpackChunkName: "about" */ '../views/Home'),
    children:[
      {
        path: '/table',
        name: 'table',
        component: () => import(/* webpackChunkName: "about" */ '../views/Table')
      },
      {
        path: '/erd',
        name: 'erd',
        component: () => import(/* webpackChunkName: "about" */ '../views/ERD')
      },
      {
        path: '/graph',
        name: 'graph',
        component: () => import(/* webpackChunkName: "about" */ '../views/Graph')
      }
    ]
  },{
    path: '/chart',
    name: 'chart',
    component: () => import(/* webpackChunkName: "about" */ '../views/Chart'),
    children:[
      {
        path: '/chart/barchart',
        name: 'barchart',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/BarChart')
      },
      {
        path: '/chart/calendar',
        name: 'calendar',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/Calendar')
      },
      {
        path: '/chart/scatterdiagram',
        name: 'scatterdiagram',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/ScatterDiagram')
      },
      {
        path: '/chart/bubblechart',
        name: 'bubblechart',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/BubbleChart')
      },
      {
        path: '/chart/choroplethmap',
        name: 'choroplethmap',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/ChoroplethMap')
      },
      {
        path: '/chart/donutchart',
        name: 'donutchart',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/DonutChart')
      },
      {
        path: '/chart/nesteddonutchart',
        name: 'nesteddonutchart',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/NestedDonutChart')
      },
      {
        path: '/chart/wordcloud',
        name: 'wordcloud',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/WordCloud')
      },
      {
        path: '/chart/lineChart',
        name: 'linechart',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/LineChart')
      },
      {
        path: '/chart/stackedbarchart',
        name: 'stackedbarchart',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/StackedBarChart')
      },
      {
        path: '/chart/groupedbarchart',
        name: 'groupedbarchart',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/GroupedBarChart')
      },
      {
        path: '/chart/spiderchart',
        name: 'spiderchart',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/SpiderChart')
      },
      {
        path: '/chart/treemap',
        name: 'treemap',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/TreeMap')
      },
      {
        path: '/chart/hierarchytree',
        name: 'hierarchytree',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/HierarchyTree')
      },
      {
        path: '/chart/circlepacking',
        name: 'circlepacking',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/CirclePacking')
      },
      {
        path: '/chart/sankey',
        name: 'sankey',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/Sankey')
      },
      {
        path: '/chart/chord',
        name: 'chord',
        component: () => import(/* webpackChunkName: "about" */ '../views/charts/Chord')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
