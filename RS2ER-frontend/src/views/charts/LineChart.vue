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
                let parentPkData = {};
                let parentPks = [];
                for (const index in jsonData) {
                    if (jsonData[index][parentPksName] in parentPkData) {
                        parentPkData[jsonData[index][parentPksName]].push(jsonData[index])
                    } else {
                        if (Object.getOwnPropertyNames(parentPkData).length >= 20) {
                            continue
                        }
                        parentPks.push(jsonData[index][parentPksName])
                        parentPkData[jsonData[index][parentPksName]] = [jsonData[index]]
                    }
                }
                let attr = route.query['attributes'][0]
                const dataset = [];
                const seriesList = [];
                echarts.util.each(parentPks, function (parentPk) {
                    dataset.push({
                        id: parentPk,
                        source: parentPkData[parentPk]
                    });
                    seriesList.push({
                        type: 'line',
                        datasetId: parentPk,
                        symbolSize: 8,
                        name: parentPk,
                        encode: {
                            x: childPksName,
                            y: attr,
                            label: parentPk,
                            itemName: childPksName,
                            tooltip: [attr]
                        }
                    });
                });
                var option = {
                    tooltip: {
                        formatter: function (params) {
                            return (
                                parentPksName + ': ' + params.data[parentPksName] + ' ' +
                                childPksName + ' : ' + params.data[childPksName] + ' ' +
                                attr + ' : ' + params.data[attr]
                            );
                        }
                    },
                    legend: {
                        show: true,
                        right: 0
                    },
                    xAxis: {
                        type: 'value',
                        name: childPksName,
                        nameLocation: 'middle',
                        scale: true,
                        nameTextStyle: {
                            color: 'rgba(5,0,0,0.78)',
                            fontSize: 18,
                            padding: [30, 0, 0, 0]
                        },
                        axisLabel: {rotate: 50, interval: 0}
                    },
                    yAxis: {
                        type: 'value',
                        name: attr,
                        nameLocation: 'middle',
                        scale: true,
                        nameTextStyle: {
                            color: 'rgba(5,0,0,0.78)',
                            fontSize: 18,
                            padding: [0, 0, 80, 0]
                        },
                    },
                    dataset: dataset,
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