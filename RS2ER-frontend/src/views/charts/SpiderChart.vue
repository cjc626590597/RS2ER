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
    import IndicatorAxis from "echarts/types/src/coord/radar/IndicatorAxis";

    const route = useRoute()
    const chart = ref()
    const title = ref()

    onMounted(
        () => {
            generate()
        }
    )

    // Generate the chart
    function generate() {
        var myChart = echarts.init(chart.value);
        if (typeof route.query['attributes'] == "string") {
            route.query['attributes'] = [route.query['attributes']]
        }
        axios.put("http://localhost:8182/graph/chart", route.query).then(function (resp) {
            if (typeof resp.data != "string") {
                let parentPksName = resp.data["parentPksName"]
                let childPksName = resp.data["childPksName"]
                let jsonData = resp.data["jsonData"]
                if (route.query['choice'] == "attributes") {
                    title.value = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + ' for attributes (' + route.query['attributes'] + ')'
                } else {
                    if (route.query['type'] == 'one-many' || route.query['type'] == 'many-many') {
                        title.value = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ' relationship: ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    } else {
                        title.value = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ': ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    }
                }
                let attr = route.query['attributes'][0]
                let parentPkData = {};
                let childPks = new Set();
                let maxVal = 0;
                for (const index in jsonData) {
                    if (jsonData[index][parentPksName] in parentPkData) {
                        const childPk = jsonData[index][childPksName]
                        if (!(childPk in childPks) && Object.getOwnPropertyNames(childPks).length >= 20) {
                            continue
                        }
                        childPks.add(childPk)
                        maxVal = Math.max(maxVal, jsonData[index][attr]);
                        parentPkData[jsonData[index][parentPksName]][childPk] = jsonData[index][attr]
                    } else {
                        if (Object.getOwnPropertyNames(parentPkData).length >= 20) {
                            continue
                        }
                        const childPk = jsonData[index][childPksName]
                        if (!(childPk in childPks) && Object.getOwnPropertyNames(childPks).length >= 20) {
                            continue
                        }
                        childPks.add(childPk)
                        maxVal = Math.max(maxVal, jsonData[index][attr]);
                        parentPkData[jsonData[index][parentPksName]] = {}
                        parentPkData[jsonData[index][parentPksName]][parentPksName] = jsonData[index][parentPksName]
                        parentPkData[jsonData[index][parentPksName]][childPk] = jsonData[index][attr]
                    }
                }
                const newParentPks = []
                for (const key in parentPkData) {
                    newParentPks.push(key)
                }
                const dataset = [];
                echarts.util.each(newParentPks, function (parentPk) {
                    dataset.push({
                        value: getOnlyValues(Array.from(childPks), parentPkData[parentPk]),
                        name: parentPk
                    });
                });
                const indicators = []
                for (const childPk of Array.from(childPks)) {
                    indicators.push({name: childPk, max: maxVal})
                }
                var option = {
                    tooltip: {
                        trigger: 'item'
                    },
                    legend: {
                        show: true,
                        right: 0
                    },
                    radar: {
                        // shape: 'circle',
                        indicator: indicators
                    },
                    series: [
                        {
                            // name: 'Budget vs spending',
                            type: 'radar',
                            data: dataset
                        }
                    ]
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

    function getOnlyValues(childPks, record) {
        var ret = []
        for (var childPk of childPks) {
            if (!(childPk in record)) {
                ret.push(0)
            } else {
                ret.push(record[childPk])
            }
        }
        return ret
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
        display: flex;
        justify-content: center;
        width: 1200px;
        height: 600px;
    }
</style>