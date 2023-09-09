<template>
    <el-container>
        <div class="title">
            <p>{{title}}</p>
        </div>
        <div class="chart">
            <GoogleChart/>
        </div>
    </el-container>
</template>
<script>

    import {defineComponent, h} from 'vue';
    import {GChart} from 'vue-google-charts';

    import {useRoute} from "vue-router"//

    import axios from "axios";
    import {Action, ElMessage, ElMessageBox} from "element-plus";

    export const type = 'Calendar';
    export let title = '';
    export let data = [
        [
            {
                type: 'date',
                id: 'Date',
            },
            {
                type: 'number',
                id: 'Value',
            },
            {
                type: 'string',
                role: 'tooltip'
            }
        ]
    ];

    export const options = {
        width: 800,
        height: 600,
        tooltip: {isHtml: true},
    };

    export default defineComponent({
        name: 'GoogleChart',
        components: {
            GChart,
        },
        setup() {
            const route = useRoute()
            if (typeof route.query['attributes'] == "string") {
                route.query['attributes'] = [route.query['attributes']]
            }
            axios.put("http://localhost:8182/graph/chart", route.query).then(function (resp) {
                if (typeof resp.data != "string") {
                    let pksName = resp.data["pksName"]
                    let jsonData = resp.data["jsonData"].slice(5)
                    let attr0 = route.query['attributes'][0]
                    if (route.query['choice'] == "attributes") {
                        title = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + ' for attributes (' + route.query['attributes'] + ')'
                    } else {
                        if (route.query['type'] == 'one-many' || route.query['type'] == 'many-many') {
                            title = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ' relationship: ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                        } else {
                            title = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ': ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                        }
                    }
                    for (const index in jsonData) {
                        if (typeof jsonData[index][attr0] == 'string') {
                            const record = []
                            const day = new Date(Date.parse(jsonData[index][attr0].replace(/-/g, "/")));
                            record.push(day)
                            record.push(1000)
                            record.push("Date: " + jsonData[index][attr0] + " " + pksName + ": " + jsonData[index][pksName])
                            data.push(record)
                        }
                    }
                    data.push([new Date(Date.parse('2023-01-01'.replace(/-/g, "/"))), 0, 'test'])
                } else {
                    ElMessageBox.alert(resp.data, 'Error', {
                        // if you want to disable its autofocus
                        // autofocus: false,
                        confirmButtonText: 'OK',
                        callback: () => {
                            ElMessage({
                                type: 'info',
                                message: `action: ${action}`,
                            })
                        },
                    })
                }
            })
            console.log(title)
            return () =>
                h(GChart, {
                    data,
                    options,
                    type,
                    settings: {
                        packages: ['calendar'],
                    },
                    title
                });
        },
    });
</script>

<style scoped>
    .chart {
        display: flex;
        justify-content: center;
        width: 1200px;
        height: 600px;
    }
</style>