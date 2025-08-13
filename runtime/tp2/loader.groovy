#META:

// Shared loader:
//  Loads a database, in .GraphSON or .xml format, into a graph server
//   via graph object _g_ provided by header.groovy
//  Maintainer: Brugnara

def DATASET_FILE = System.env.get("DATASET")
def DEBUG = System.env.get("DEBUG") != null

def loadTxtGraph(filename, vertexNum, isUndirected, graphToWrite) {
    nsToS = 1000000000;
    System.err.println("loading txt: ${filename}");
    def vs = [];
    t1 = System.nanoTime();
    // totalVertices = 41652230;
    totalVertices = vertexNum;
    // totalVertices = 20000000;
    for (int i = 0; i < totalVertices; i++) {
        vs.add(graphToWrite.addVertex(null) as Vertex);
        if (i % 1000000 == 0) {
            try {
                graphToWrite.commit();
            } catch (MissingMethodException e) {
                // do nothing..
            }
            t2 = System.nanoTime();
            System.err.println("loaded ${i} vertices, spend time: ${(t2 - t1) / nsToS} s");
        }
    }
    try {
        System.err.println("prepare to commit");
        graphToWrite.commit();
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
            data = "${i} ${vs[i].id.toString()}\n";
            out.write(data.getBytes());
        }
        out.close();
    } catch (Exception e) {
        // do nothing
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
            vs[idx1].addEdge("edge", vs[idx2]);
            if (isUndirected) {
                vs[idx2].addEdge("edge", vs[idx1]);
            }
            idx += 1;
            if (idx % 1000000 == 0) {
                try {
                    graphToWrite.commit();
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

def stime = System.currentTimeMillis()
if (DATASET_FILE.endsWith('.xml'))
    g.loadGraphML(DATASET_FILE)
else if (DATASET_FILE.contains('twitter-2010') || DATASET_FILE.contains('fake')) {
    loadTxtGraph(DATASET_FILE, 41652230, false, g)
} else if (DATASET_FILE.contains('com-dblp.ungraph')) {
    loadTxtGraph(DATASET_FILE, 425957, true, g)
} else if (DATASET_FILE.contains('com-orkut.ungraph')) {
    loadTxtGraph(DATASET_FILE, 3072627, true, g)
} else if (DATASET_FILE.contains("wikipedia")) {
    loadTxtGraph(DATASET_FILE, 3333397, false, g);
} else if (DATASET_FILE.contains("wikitalk")) {
    loadTxtGraph(DATASET_FILE, 2394386, false, g);
} else if (DATASET_FILE.contains('cit-patents')) {
    loadTxtGraph(DATASET_FILE, 3774769, false, g);
} else {
    System.err.println("Start loading")
    g.loadGraphSON(DATASET_FILE)
    System.err.println("End loading")
}
def exec_time = System.currentTimeMillis() - stime

try {
    g.commit();
} catch (MissingMethodException e) {
    System.err.println("Does not support g.commit(). Ignoring.");
}

if (DEBUG) {
    v = g.V.count();
    e = g.E.count();
    System.err.println(DATABASE + " loaded V: " + v + " E: " + e)
}

result_row = [DATABASE, DATASET, QUERY,'','','',String.valueOf(exec_time)]
System.out.println(result_row.join(','));


