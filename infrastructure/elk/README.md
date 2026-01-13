# ELK Stack Setup

This directory contains a Docker Compose configuration for running an ELK (Elasticsearch, Logstash, Kibana) stack with Filebeat for centralized logging.

## Prerequisites

- Docker
- Docker Compose

## Quick Start

1. Start the ELK stack:
   ```bash
   docker-compose up -d
   ```

2. Wait for services to be healthy (this may take a minute or two):
   ```bash
   docker-compose ps
   ```

3. Access Kibana:
   - Open your browser and navigate to: http://localhost:5601
   - Kibana should be ready to use

4. Verify Elasticsearch is running:
   ```bash
   curl http://localhost:9200
   ```

## Services

- **Elasticsearch**: Runs on port 9200 (HTTP) and 9300 (transport)
- **Logstash**: Listens on ports 5044 (Beats), 5000 (TCP/UDP for logs), and 9600 (monitoring)
- **Kibana**: Web UI on port 5601
- **Filebeat**: Collects logs from Docker containers and log files, sends to Logstash

## Adding MSA Services

1. Add your service to `docker-compose.yml` in the section marked "Space for MSA services"
2. Connect your service to the `elk-network` network
3. Mount your service's log directory to Filebeat:
   - Uncomment and modify the volume mount in the `filebeat` service section
   - Example: `- ../backend/logs:/var/log/msa-services:ro`
4. Add a corresponding input in `filebeat/filebeat.yml`:
   ```yaml
   - type: log
     enabled: true
     paths:
       - '/var/log/msa-services/*.log'
     fields:
       service: your-service-name
   ```

## Stopping the Stack

```bash
docker-compose down
```

To remove volumes (this will delete all indexed data):
```bash
docker-compose down -v
```

## Filebeat Configuration

Filebeat is configured to:
- Automatically collect logs from all Docker containers
- Send logs to Logstash on port 5044
- Add Docker metadata to log entries

To add custom log file paths for your MSA services:
1. Mount the log directory in the `filebeat` service volumes section
2. Add a corresponding input configuration in `filebeat/filebeat.yml`

## Troubleshooting

- If services fail to start, check logs: `docker-compose logs`
- Elasticsearch may take 30-60 seconds to become healthy
- Ensure ports 9200, 5601, 5044, and 5000 are not in use by other applications
- Filebeat requires root access to read Docker container logs (configured in docker-compose.yml)
- Check Filebeat logs: `docker-compose logs filebeat`