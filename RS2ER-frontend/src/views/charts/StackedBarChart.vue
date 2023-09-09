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
                console.log(resp.data)
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
                for (const index in jsonData) {
                    if (jsonData[index][parentPksName] in parentPkData) {
                        const childPk = jsonData[index][childPksName]
                        if (!(childPk in childPks) && Object.getOwnPropertyNames(childPks).length >= 20) {
                            continue
                        }
                        childPks.add(childPk)
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
                        parentPkData[jsonData[index][parentPksName]] = {}
                        parentPkData[jsonData[index][parentPksName]][parentPksName] = jsonData[index][parentPksName]
                        parentPkData[jsonData[index][parentPksName]][childPk] = jsonData[index][attr]
                    }
                }
                const newParentPkData = []
                for (const key in parentPkData) {
                    newParentPkData.push(parentPkData[key])
                }
                const seriesList = [];
                echarts.util.each(Array.from(childPks).sort(), function (childPk) {
                    seriesList.push({
                        name: childPk,
                        type: 'bar',
                        stack: 'total',
                        barGap: 0,
                        emphasis: {
                            focus: 'series'
                        },
                        data: getParamValues(childPk, newParentPkData)
                    });
                });
                var option = {
                    tooltip: {
                        formatter: function (params) {
                            return (
                                parentPksName + ': ' + newParentPkData[params.dataIndex][parentPksName] + ' ' +
                                params["seriesName"] + ' : ' + params["value"]
                            );
                        }
                    },
                    legend: {
                        show: true
                    },
                    xAxis: [
                        {
                            type: 'category',
                            axisTick: {show: false},
                            data: getParamValues(parentPksName, newParentPkData),
                            name: parentPksName,
                            nameTextStyle: {
                                color: 'rgba(5,0,0,0.78)',
                                fontSize: 18,
                                padding: [0, 0, 0, 150]
                            },
                            axisLabel: {rotate: 50, interval: 0},
                            nameRotate: 90
                        }
                    ],
                    yAxis: [
                        {
                            type: 'value',
                            scale: true,
                            name: route.query['attributes'],
                            nameTextStyle: {
                                color: 'rgba(5,0,0,0.78)',
                                fontSize: 18,
                                padding: [0, 100, 0, 0]
                            }
                        }
                    ],
                    series: seriesList
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

    function getParamValues(name, data) {
        var ret = []
        for (var i = 0, len = data.length; i < len; i++) {
            if (!(name in data[i])) {
                ret.push(0)
            } else {
                ret.push(data[i][name])
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