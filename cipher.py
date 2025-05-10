import base64
import argparse
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad
import hashlib

def get_key(password):
    return hashlib.sha256(password.encode()).digest()

def encrypt(text, password):
    key = get_key(password)
    cipher = AES.new(key, AES.MODE_CBC)
    ct_bytes = cipher.encrypt(pad(text.encode(), AES.block_size))
    iv = base64.b64encode(cipher.iv).decode()
    ct = base64.b64encode(ct_bytes).decode()
    return f"{iv}:{ct}"

def decrypt(data, password):
    try:
        key = get_key(password)
        iv, ct = data.split(":")
        iv = base64.b64decode(iv)
        ct = base64.b64decode(ct)
        cipher = AES.new(key, AES.MODE_CBC, iv)
        pt = unpad(cipher.decrypt(ct), AES.block_size)
        return pt.decode()
    except Exception as e:
        return f"解密失败: {e}"

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="文本加密/解密工具")
    parser.add_argument("mode", choices=["encrypt", "decrypt"], help="选择操作模式")
    parser.add_argument("text", help="要加密或解密的文本")
    parser.add_argument("--password", required=True, help="加密/解密密码")

    args = parser.parse_args()
    
    if args.mode == "encrypt":
        result = encrypt(args.text, args.password)
        print(f"加密结果: {result}")
    else:
        result = decrypt(args.text, args.password)
        print(f"解密结果: {result}")
