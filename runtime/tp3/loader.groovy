#META:

// Shared loader:
//  Loads a database, in .GraphSON or .xml format, into a graph server
//   via graph object _g_ provided by header.groovy
//  Maintainer: Lissandrini


def loadTxtGraph(filename, vertexNum, isUndirected, graphToWrite) {
    nsToS = 1000000000;
    System.err.println("loading txt: ${filename}");
    def vs = [];
    t1 = System.nanoTime();
    totalVertices = vertexNum;
    // totalVertices = 20000000;
    for (int i = 0; i < totalVertices; i++) {
        vs.add(graphToWrite.addVertex("vertex") as Vertex);
        if (i % 1000000 == 0) {
            graphToWrite.tx().commit();
            t2 = System.nanoTime();
            System.err.println("loaded ${i} vertices, spend time: ${(t2 - t1) / nsToS} s");
        }
    }
    try {
        System.err.println("prepare to commit");
        graphToWrite.tx().commit();
        t2 = System.nanoTime();
    } catch (MissingMethodException e) {
        // do nothing..
    }
    System.err.println("loaded all vertices, spend time: ${(t2 - t1) / nsToS} s");
    dbName = System.env.get("DBNAME");
    graphName = System.env.get("GRAPHNAME");
    outputFile = "/runtime/meta/graphid/" + dbName + "." + graphName + ".mapping";
    try {
        out = new FileOutputStream(outputFile);
        for (int i = 0; i < totalVertices; i++) {
            data = "${i} ${vs[i].id().toString()}\n";
            out.write(data.getBytes());
        }
        out.close();
    } catch (Exception e) {
        // do nothing
        System.err.println(e);
    }
    is = new FileReader(filename);
    reader = new BufferedReader(is);
    idx = 0;
    try {
        String line = null;
        while ((line = reader.readLine()) != null) {
            // System.err.println(line);
            String[] evs = line.split(" ");
            idx1 = evs[0].toInteger();
            idx2 = evs[1].toInteger();
            vs[idx1].addEdge("edg", vs[idx2]);
            if (isUndirected) {
                vs[idx2].addEdge("edg", vs[idx1]);
            }
            idx += 1;
            if (idx % 1000000 == 0) {
                try {
                    graphToWrite.tx().commit();
                } catch (MissingMethodException e) {
                    // do nothing
                }
                t2 = System.nanoTime();
                System.err.println("loaded ${idx} edges, spend time: ${(t2 - t1) / nsToS} s");
            }
        }
        t2 = System.nanoTime();
        System.err.println("loaded all edges, spend time: ${(t2 - t1) / nsToS} s")
    } finally {
        is.close();
    }
}

DEBUG = System.env.get("DEBUG") != null
System.err.println(DATASET)
System.err.println("Start loading");
stime = System.currentTimeMillis();
try{

#BLAZEgraph.setBulkLoad(true);

    if (DATASET.endsWith('.xml')) {
        g.loadGraphML(DATASET);
    } else if (DATASET.endsWith('twitter-2010.json3') || DATASET.endsWith('fake.json3')) {
        loadTxtGraph(DATASET, 41652230, false, graph);
    } else if (DATASET.contains('com-dblp.ungraph.json3')) {
        // for pg
        DATASET = '/runtime/data/com-dblp.ungraph.json3'
        loadTxtGraph(DATASET, 425957, true, graph);
    } else if (DATASET.contains('com-orkut.ungraph')) {
        DATASET = '/runtime/data/com-orkut.ungraph.json3'
        loadTxtGraph(DATASET, 3072627, true, graph);
    } else if (DATASET.contains('twitch')) {
        DATASET = '/runtime/data/twitch.json3'
        loadTxtGraph(DATASET, 168115, true, graph);
    } else if (DATASET.contains('berkstan')) {
        DATASET = '/runtime/data/berkstan.json3'
        loadTxtGraph(DATASET, 685231, false, graph);
    } else if (DATASET.contains('topcats')) {
        DATASET = '/runtime/data/topcats.json3'
        loadTxtGraph(DATASET, 1791489, false, graph);
    } else if (DATASET.contains('pokec')) {
        DATASET = '/runtime/data/pokec.json3'
        loadTxtGraph(DATASET, 1632804, false, graph);
    } else if (DATASET.contains('journal')) {
        DATASET = '/runtime/data/journal.json3'
        loadTxtGraph(DATASET, 4847572, false, graph);
    } else if (DATASET.contains('dbpedia')) {
        DATASET = '/runtime/data/dbpedia.json3'
        loadTxtGraph(DATASET, 18268993, false, graph);
    } else if (DATASET.contains('wikipedia')) {
        DATASET = '/runtime/data/wikipedia.json3'
        loadTxtGraph(DATASET, 3333398, false, graph);
    } else if (DATASET.contains('wikitalk')) {
        DATASET = '/runtime/data/wikitalk.json3'
        loadTxtGraph(DATASET, 2394386, false, graph);
    } else if (DATASET.contains('cit-patents')) {
        DATASET = '/runtime/data/cit-patents.json3'
        loadTxtGraph(DATASET, 3774769, false, graph);
    }
    else if (DATASET.endsWith('.json3')) {
        final InputStream is = new FileInputStream(DATASET)
        final GraphSONMapper mapper = graph.io(IoCore.graphson()).mapper().create()
        //graph.io(IoCore.graphson()).writer().mapper(mapper).create().writeGraph(os, graph);
        graph.io(IoCore.graphson()).reader().create().readGraph(is, graph)
        is.close();
    } else {
#SQLG   graph.tx().normalBatchModeOn();
        LegacyGraphSONReader r = LegacyGraphSONReader.build().batchSize(2_000_000).create();
                          // r = LegacyGraphSONReader.build().create()
        InputStream stream ;
        try{
            stream = new FileInputStream(DATASET);
            // r.readGraph(new FileInputStream(DATASET), graph)
            r.readGraph(stream, graph);
        } finally {
            stream.close();
        }
    }

    t1 = System.nanoTime()
    try {
        graph.tx().commit();
    } catch (MissingMethodException e) {
        System.err.println("Does not support g.tx().commit(). Ignoring.");
    }
    t2 = System.nanoTime();
    System.err.println("commit time: ${(t2 - t1) / 1000000000} s");

#BLAZEgraph.setBulkLoad(false);


}catch( Exception e){

    traceFile = "/runtime/logs/loader.exception."+System.currentTimeMillis()+".trace"

    fos = new FileOutputStream(new File(traceFile), true);
    ps = new PrintStream(fos);
    e.printStackTrace(ps);
    fos.close()
    System.err.println("Error " + e.getMessage());
    System.exit(2)

}

exec_time = System.currentTimeMillis() - stime

System.err.println("End loading");

result_row = [DATABASE, DATASET, QUERY,'','','',String.valueOf(exec_time)]
println result_row.join(',')


if (DEBUG) {
    System.err.println(" ########################################## DEBUG ");
    g = graph.traversal()
    vid = g.V().next().id();
    System.err.println(" First node is " + vid);
    System.err.print("Stats: Nodes... ");
    v = g.V().count().next();
    System.err.println("& Edges Nodes");
    e = g.E().count().next();
    System.err.println(DATABASE + " loaded V: " + v + " E: " + e);
    System.err.println(" ########################################## DEBUG ");

}
t2 = System.nanoTime();
System.err.println("stats finished time: ${(t2 - t1) / 1000000000} s");