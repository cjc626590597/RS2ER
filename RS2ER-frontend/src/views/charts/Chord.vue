<template>
    <el-container>
        <div>
            <div id="chart"
                 style="width: 1200px; height: 600px">
            </div>
        </div>
    </el-container>
</template>


<script lang="ts" setup>
    import {useRoute} from "vue-router"//

    import {ref, onMounted} from "vue";
    import axios from "axios";
    import {Action, ElMessage, ElMessageBox} from "element-plus";
    import FusionCharts from "fusioncharts";

    const route = useRoute()
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
                let firstPKsName = resp.data["firstPKsName"]
                let secondPKsName = resp.data["secondPKsName"]
                let jsonData = resp.data["jsonData"]
                let attr = route.query['attributes'][0]
                let title = ""
                if (route.query['choice'] == "attributes") {
                    title = 'Chord Diagram -- ' + 'Entity ' + route.query['relation'] + ' for attributes (' + route.query['attributes'] + ')'
                } else {
                    if (route.query['type'] == 'one-many' || route.query['type'] == 'many-many') {
                        title = 'Chord Diagram -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ' relationship: ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    } else {
                        title = 'Chord Diagram -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ': ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    }
                }
                let dataSet = new Set();
                let links = [];
                for (const index in jsonData) {
                    const firstPK = jsonData[index][firstPKsName];
                    const secondPK = jsonData[index][secondPKsName];
                    if (dataSet.size >= 20) {
                        if (firstPK in dataSet && secondPK in dataSet) {
                            const link = {}
                            link['from'] = firstPK
                            link['to'] = secondPK
                            link['value'] = jsonData[index][attr]
                            links.push(link)
                        }
                    } else {
                        dataSet.add(firstPK)
                        dataSet.add(secondPK)
                        const link = {}
                        link['from'] = firstPK
                        link['to'] = secondPK
                        link['value'] = jsonData[index][attr]
                        links.push(link)
                    }
                }
                const nodes = []
                for (var key of Array.from(dataSet.values())) {
                    let data = {'label': key}
                    nodes.push(data)
                }
                const dataSource = {
                    chart: {
                        caption: title,
                        nodelabelposition: "outside",
                        showlegend: 0,
                        theme: "candy",
                        mode: "post",
                        linkcolorbydominance: "1",
                        animation: "0"
                    },
                    nodes: nodes,
                    links: links
                };
                FusionCharts.ready(function () {
                    var myChart = new FusionCharts({
                        type: "chord",
                        renderAt: "chart",
                        width: "100%",
                        height: "100%",
                        dataFormat: "json",
                        dataSource
                    }).render();
                });
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
    #chart {
        display: flex;
        justify-content: center;
        width: 1200px;
        height: 600px;
    }
</style>