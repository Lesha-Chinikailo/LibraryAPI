#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE book_service_database;
    CREATE DATABASE bookLibrary;
EOSQL
