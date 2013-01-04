lightmare
=========

Embeddable ejb container (works for stateless session beans)

# Overview
==========

Lightmare is lightweight library to run ejb project in any type application without any ejb containers


# Use it!
=========

Usage strts with parsing eny directory, jar or ear file to find and register stateless session beans (with scannotation http://scannotation.sourceforge.net/)
and create and cache [EntityManagerFactory] for each [@PersistenceContext] annotated field in bean
