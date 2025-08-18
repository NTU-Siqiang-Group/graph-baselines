# Baselines Test Suite for Aster

**This repository contains the code for the following publication:**

> **Aster: Enhancing LSM-structures for Scalable Graph Database**  
> *Dingheng Mo, Junfeng Liu, Fan Wang, Siqiang Luo*  
> *Proceedings of the ACM on Management of Data (SIGMOD 2025)*

This repository provides the tools and instructions to reproduce the experimental results for baseline systems discussed in the paper. The following graph database systems are included:

1. Neo4j  
2. OrientDB  
3. ArangoDB  
4. JanusGraph  
5. SQLG  

---

## Prerequisites

- [Anaconda](https://www.anaconda.com/)
- [Docker](https://www.docker.com/)

---

## Step 1: Build Docker Images

All experiments are conducted using Docker for consistency and portability. Please ensure Docker is installed and running on your machine.

```bash
./build_images.sh
```

## Step2: Set Up the Python Environment
We use Anaconda to manage the testing environment and dependencies. Run the following script to prepare the environment:

## Step 3: Download the Datasets
Run the script below to download all required graph datasets:
```bash
./download_graphs.sh
```

## Step 4: Run Experiments
Each experiment script corresponds to a figure in the paper. For example, `fig6.sh`reproduces the results shown in Figure 6. To reproduce all results, run the scripts in the following order:
```bash
./fig6.sh
./fig7.sh
./fig7_property.sh
```