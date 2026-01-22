#!/bin/bash
# Uses Ingress Controller for single-point access
# Run on App EC2 to expose services externally

set -e

KUBECONFIG_PATH="/home/ubuntu/.kube/config"

echo "=== Stopping any existing port-forwards ==="
pkill -f "kubectl port-forward" 2>/dev/null || true
sleep 2

echo "=== Starting Ingress Controller port-forward ==="
# Single port-forward to Ingress Controller handles all routing
# This survives pod restarts since it connects to the service, not pod
sudo KUBECONFIG=$KUBECONFIG_PATH kubectl port-forward --address 0.0.0.0 svc/ingress-nginx-controller 443:443 -n ingress-nginx > /dev/null 2>&1 &

sleep 3

echo ""
echo "=== Ingress port-forward active ==="
ps aux | grep "kubectl port-forward" | grep -v grep
echo "All routes handled by Ingress - survives pod restarts!"
echo "To stop: pkill -f 'kubectl port-forward'"