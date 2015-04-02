#!/usr/bin/python
from py2neo import Graph, Node, Relationship 
import uuid
import commons
import codecs

#Conexion a Neo4j 
graph = Graph("http://localhost:7474/db/data/")
try:
    graph.schema.create_uniqueness_constraint("EventCategory", "uuid")
except:
    print "Contraint uuid ya creada"

with codecs.open('categories.csv','r', 'utf-8') as f:
    for line in f:
        csv =  line.rstrip().split(',')
        category = csv[0]
        hyphen = commons.get_hyphen_name(category)
        color = csv[1]
        uuid = commons.get_uuid('mx.oiga/events/categories/'+hyphen)
        path = '/'+hyphen
        properties = {
               'name' : category,
               'hyphen' : hyphen,
               'uuid' : uuid,
               'path' : path,
               'icon' : 'glyphicon-calendar',
               'color' : color
                }
        node = Node.cast(properties)
        node.labels.add("EventCategory")
        graph.create(node)
