# open-health

Open Health Demo

## Demo/Lab Developers:

*Faz Sadeghi*, EMEA OpenShift Specialist Solution Architect, Red Hat

*Cesar Fernandez*, EMEA Ansible Specialist Solution Architect, Red Hat

## Prerequisites

- OpenShift Cluster +4.12 with admin rights.
- Ansible Navigator.
- Demo Environment project.

### OpenShift

If you don't have an OpenShift Cluster 4.12 with admin rights, you can request one in the following [link](https://demo.redhat.com/catalog?category=Workshops&item=babylon-catalog-prod%2Fsandboxes-gpte.ocp412-wksp.prod).

### Ansible Navigator

- If you have Linux, you can install the `ansible-navigator` tool following the steps described in the following [link](https://ansible-navigator.readthedocs.io/installation/#linux).

> Note: Run the command `ansible-navigator --version` to make sure it was installed correctly.

- After installing it, create the `inventory` and the `ansible-navigator` definition file:

```sh
cd ~
mkdir ansible-navigator
cat << EOF > ansible-navigator/inventory
[controller]
localhost ansible_connection=local
EOF
cat << EOF > ansible-navigator/ansible-navigator.yml
---
ansible-navigator:
  ansible:
    inventory:
      entries:
      - ./inventory
  app: run
  editor:
    command: vim_from_setting
    console: false
  execution-environment:
    container-engine: podman
    image: quay.io/ansible_eda/ansible-eda-ee:1.0
    pull:
      policy: missing
  logging:
    append: true
    file: /tmp/navigator/ansible-navigator.log
    level: debug
  playbook-artifact:
    enable: false
EOF
```

## How to deploy the demo environment

### Demo Environment Project

- Clone the demo environment project:

```sh
cd ~
git clone https://github.com/fazifs/open-health.git
```

### Deploy Demo Environment

- Create the demo configuration variables file, replacing the highlighted variables:

```sh
cat << EOF > iac/vars/demo-config.yml
---
# Demo vars
ansible_demo_namespace: ansible-demo
ansible_demo_templates_path: templates

# OCP vars
ocp_templates_path: "{{ ansible_demo_templates_path }}/images"

# Ansible EDA vars
eda_templates_path: "{{ ansible_demo_templates_path }}/ansible-eda"

# AMQ Streams vars
amq_streams_operator_templates_path: "{{ ansible_demo_templates_path }}/amq-streams/operator"
amq_streams_instance_templates_path: "{{ ansible_demo_templates_path }}/amq-streams/instance"

# Telegram vars
telegram_token: "<TELEGRAM_TOKEN>"
telegram_chat_id: "<TELEGRAM_CHAT_ID>"

# Ansible vars
ansible_operator_templates_path: "{{ ansible_eda_demo_templates_path }}/ansible/operator"
ansible_instance_templates_path: "{{ ansible_eda_demo_templates_path }}/ansible/instance"
ansible_instance_admin_password: redhat
ansible_instance_manifest: "*<AAP_MANIFEST>*" --> !!!ENCODE IN BASE64!!!
```

> Note: In order to obtain a subscription manifest, please follow the steps described on this link: https://docs.ansible.com/ansible-tower/latest/html/userguide/import_license.html#obtaining-a-subscriptions-manifest

- Deploy the demo environment:

```sh
cd ~/ansible-navigator
ansible-navigator run ../open-health/iac/main.yml -m stdout \
  -e 'ansible_python_interpreter=/usr/bin/python3' \
  -e 'openshit_api=<OPENSHIFT_API_URL>' \
  -e 'openshift_token=<OPENSHIFT_TOKEN>' \
  -e 'openshit_storage_class=gp3-csi'
```
