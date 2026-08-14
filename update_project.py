import os

project_dir = "/home/ubuntu/v2raySA/V2rayNG"

def replace_in_files(old_str, new_str):
    for root, dirs, files in os.walk(project_dir):
        for file in files:
            if file.endswith(('.kt', '.xml', '.gradle', '.kts', '.properties', '.json')):
                file_path = os.path.join(root, file)
                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        content = f.read()
                    if old_str in content:
                        new_content = content.replace(old_str, new_str)
                        with open(file_path, 'w', encoding='utf-8') as f:
                            f.write(new_content)
                except Exception as e:
                    pass

print("Starting replacement...")
replace_in_files("com.v2ray.ang", "com.samal.v2ray")
replace_in_files("v2rayNG", "SAMAL V2RAY")
replace_in_files("2dust", "@libsammal")
print("Replacement completed.")
