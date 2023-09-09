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
                let pksName = resp.data["pksName"]
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
                let data = [];
                let maxVal = Number.NEGATIVE_INFINITY
                let minVal = Number.MAX_SAFE_INTEGER
                for (const index in jsonData) {
                    const child = {}
                    child['name'] = jsonData[index][pksName]
                    child['value'] = jsonData[index][attr]
                    if (jsonData[index][attr] > maxVal) {
                        maxVal = jsonData[index][attr]
                    }
                    if (jsonData[index][attr] < minVal) {
                        minVal = jsonData[index][attr]
                    }
                    data.push(child)
                }
                console.log(data)
                var myChart = echarts.init(chart.value);
                const usaJson = require("../../assets/world.json")
                echarts.registerMap('world', usaJson, {});
                var option = {
                    title: {},
                    tooltip: {
                        formatter: function (params) {
                            try {
                                return (
                                    pksName + ': ' + params.data['name'] + ' ' +
                                    attr + ': ' + params.data['value']
                                );
                            } catch (e) {

                            }
                        }
                    },
                    visualMap: {
                        left: 'right',
                        // min: minVal + (maxVal-minVal)*0.2,
                        // max: maxVal - (maxVal-minVal)*0.2,
                        min: 5000000,
                        max: 300000000,
                        inRange: {
                            color: [
                                '#313695',
                                '#4575b4',
                                '#74add1',
                                '#abd9e9',
                                '#e0f3f8',
                                '#ffffbf',
                                '#fee090',
                                '#fdae61',
                                '#f46d43',
                                '#d73027',
                                '#a50026'
                            ]
                        },
                        text: ['High', 'Low'],
                        calculable: true
                    },
                    series: [
                        {
                            name: pksName,
                            type: 'map',
                            roam: true,
                            map: 'world',
                            emphasis: {
                                label: {
                                    show: true
                                }
                            },
                            data: data
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