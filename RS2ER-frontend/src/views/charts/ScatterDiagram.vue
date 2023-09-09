<template>
    <el-container>
        <div>
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
                let title = ""
                if (route.query['choice'] == "attributes") {
                    title = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + ' for attributes (' + route.query['attributes'] + ')'
                } else {
                    if (route.query['type'] == 'one-many' || route.query['type'] == 'many-many') {
                        title = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ' relationship: ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    } else {
                        title = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ': ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    }
                }
                let dimensions = [pksName].concat(route.query['attributes'])
                let attr1 = route.query['attributes'][0]
                let attr2 = route.query['attributes'][1]
                var option = {
                    title: {
                        text: title,
                        left: 'center'
                    },
                    tooltip: {
                        formatter: function (params) {
                            return (
                                pksName + ': ' + params.data[pksName] + ' ' +
                                attr1 + ' : ' + params.data[attr1] + ' ' +
                                attr2 + ' : ' + params.data[attr2]
                            );
                        }
                    },
                    legend: {
                        show: false
                    },
                    xAxis: {
                        type: 'value',
                        scale: true,
                        name: attr1,
                        nameTextStyle: {
                            color: 'rgba(5,0,0,0.78)',
                            fontSize: 18,
                            padding: [0, 100, 0, 0]
                        },
                        axisLabel: {rotate: 50, interval: 0}
                    },
                    yAxis: {
                        type: 'value',
                        scale: true,
                        name: attr2,
                        nameTextStyle: {
                            color: 'rgba(5,0,0,0.78)',
                            fontSize: 18,
                            padding: [0, 100, 0, 0]
                        },
                    },
                    dataset: {
                        dimensions: dimensions,
                        source: jsonData
                    },
                    series: [{
                        // name: pksName,
                        type: 'scatter',
                        encode: {
                            x: attr1,
                            y: attr2,
                            itemName: [1, 2]
                        },
                    }]
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
    .chart {
        display: flex;
        justify-content: center;
        width: 1200px;
        height: 600px;
    }
</style>