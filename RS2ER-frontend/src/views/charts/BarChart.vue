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
    import {useRoute} from "vue-router"
    import {ref, onMounted} from "vue";
    import * as echarts from "echarts";
    import axios from "axios";
    import {Action, ElMessage, ElMessageBox} from "element-plus";

    const route = useRoute()
    const chart = ref()
    let title = ""

    onMounted(
        () => {
            generate()
        }
    )

    // Generate the chart
    function generate() {
        var myChart = echarts.init(chart.value);
        console.log(route.query)
        if (typeof route.query['attributes'] == "string") {
            route.query['attributes'] = [route.query['attributes']]
        }
        axios.put("http://localhost:8182/graph/chart", route.query).then(function (resp) {
            if (typeof resp.data != "string") {
                let pksName = resp.data["pksName"]
                console.log(pksName)
                let jsonData = resp.data["jsonData"].slice(0, 20)
                if (route.query['choice'] == "attributes") {
                    title = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + ' for attributes (' + route.query['attributes'] + ')'
                } else {
                    if (route.query['type'] == 'one-many' || route.query['type'] == 'many-many') {
                        title = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ' relationship: ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    } else {
                        title = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ': ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    }
                }
                var option = {
                    title: {
                        text: title,
                        left: 'center'
                    },
                    tooltip: {
                        formatter: function (params) {
                            return (
                                pksName + ': ' + jsonData[params.dataIndex][pksName] + ' ' +
                                route.query['attributes'] + ' : ' + params["value"]
                            );
                        }
                    },
                    legend: {
                        show: true,
                    },
                    xAxis: {
                        data: getParamValues(pksName, jsonData),
                        name: pksName,
                        nameTextStyle: {
                            color: 'rgba(5,0,0,0.78)',
                            fontSize: 18,
                            padding: [0, 0, 0, 150]
                        },
                        axisLabel: {rotate: 50, interval: 0},
                        nameRotate: 90,
                    },
                    yAxis: {
                        name: route.query['attributes'],
                        scale: true,
                        nameTextStyle: {
                            color: 'rgba(5,0,0,0.78)',
                            fontSize: 18,
                            padding: [0, 100, 0, 0]
                        }
                    },
                    series: [
                        {
                            name: route.query['attributes'],
                            type: 'bar',
                            data: getParamValues(route.query['attributes'][0], jsonData)
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

    function getParamValues(name, data) {
        var ret = []
        for (var i = 0, len = data.length; i < len; i++) {
            ret.push(data[i][name])
        }
        return ret
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