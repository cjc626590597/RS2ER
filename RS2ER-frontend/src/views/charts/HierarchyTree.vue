<template>
    <el-container>
        <div>
            <div class="title">
                <p>{{title}}</p>
            </div>
            <div class="chart" ref="chart"
                 style="width: 1200px; height: 600px">
            </div>
        </div>
    </el-container>
</template>


<script lang="ts" setup>
    import {useRoute} from "vue-router"//

    import {ref, onMounted} from "vue";
    import * as echarts from "echarts";
    import axios from "axios";
    import {Action, ElMessage, ElMessageBox} from "element-plus";

    const route = useRoute()
    const title = ref()
    const chart = ref()

    onMounted(
        () => {
            generate()
        }
    )

    // Generate the chart
    function generate() {
        if (typeof route.query['attributes'] == "string") {
            route.query['attributes'] = [route.query['attributes']]
        }
        axios.put("http://localhost:8182/graph/chart", route.query).then(function (resp) {
            if (typeof resp.data != "string") {
                let parentPksName = resp.data["parentPksName"]
                let childPksName = resp.data["childPksName"]
                let jsonData = resp.data["jsonData"]
                let attr = route.query['attributes'][0]
                if (route.query['choice'] == "attributes") {
                    title.value = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + ' for attributes (' + route.query['attributes'] + ')'
                } else {
                    if (route.query['type'] == 'one-many' || route.query['type'] == 'many-many') {
                        title.value = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ' relationship: ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    } else {
                        title.value = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ': ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    }
                }
                let parentPkData = {};
                for (const index in jsonData) {
                    if (jsonData[index][parentPksName] in parentPkData) {
                        const child = {}
                        child['name'] = jsonData[index][childPksName]
                        child['value'] = jsonData[index][attr]
                        parentPkData[jsonData[index][parentPksName]]['children'].push(child)
                    } else {
                        if (Object.getOwnPropertyNames(parentPkData).length <= 30) {
                            parentPkData[jsonData[index][parentPksName]] = {}
                            parentPkData[jsonData[index][parentPksName]]['name'] = jsonData[index][parentPksName]
                            parentPkData[jsonData[index][parentPksName]]['children'] = []
                            const child = {}
                            child['name'] = jsonData[index][childPksName]
                            child['value'] = jsonData[index][attr]
                            parentPkData[jsonData[index][parentPksName]]['children'].push(child)
                        }
                    }
                }
                const newParentPks = []
                for (const key in parentPkData) {
                    newParentPks.push(parentPkData[key])
                }
                const allData = {}
                allData['name'] = parentPksName
                allData['children'] = newParentPks
                var myChart = echarts.init(chart.value);
                var option = {
                    tooltip: {},
                    series: {
                        type: 'tree',
                        data: [allData],
                    }
                };
                myChart.setOption(option);
            } else {
                ElMessageBox.alert(resp.data, 'Error', {
                    // if you want to disable its autofocus
                    // autofocus: false,
                    confirmButtonText: 'OK',
                    callback: (action: Action) => {
                        ElMessage({
                            type: 'info',
                            message: `action: ${action}`,
                        })
                    },
                })
            }
        })
    }
</script>

<style scoped>
    .title {
        display: flex;
        justify-content: center;
        font-size: 20px;
        font-weight: bold;
    }

    .chart {
        display: inline-block;
    }
</style>