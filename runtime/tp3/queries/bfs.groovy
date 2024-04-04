#META;

p = g.V().id();

depth = 10;

def nextStep(p, idx, round) {
  return p.out().sideEffect{print("v${idx} at round ${round}: ${it};\n")}.barrier();
}

for (int i = 0; i < 1000; i++) {
  vid1 = p.next();
  exec = g.V(vid1);
  for (int j = 0; j < depth; j++) {
    exec = nextStep(exec, i, j);
  }
  t = System.nanoTime();
  exec.fold().next();
  exec_time = System.nanoTime() - t;
  println("BFS start from " + vid1 + " finished in " + exec_time + " ns");
}
