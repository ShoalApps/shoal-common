"""Overlay a staged Maven repository, refusing changes to any published version."""
from pathlib import Path
import re
import shutil
import sys

# Every artifact each release publishes, all at the same version.
ARTIFACTS = ("shoal-ui", "shoal-expressive")


def files(directory):
    return {p.relative_to(directory): p.read_bytes() for p in directory.rglob("*") if p.is_file()}


def merge(staged, published, version):
    if not re.fullmatch(r"1[.]0[.](0|[1-9][0-9]*)", version):
        raise ValueError("Invalid release version")
    for name in ARTIFACTS:
        artifact = Path("io/github/shoalapps/common") / name
        release = staged / artifact / version
        if not release.is_dir() or not list(release.glob("*.aar")):
            raise ValueError(f"Release AAR is missing: {name}")
        # Compare every old version before copying anything, including reruns of this version.
        old_artifact = published / artifact
        if old_artifact.exists():
            for old in old_artifact.iterdir():
                if old.is_dir() and files(old) != files(staged / artifact / old.name):
                    raise ValueError(f"Refusing to overwrite published version: {name} {old.name}")
    shutil.copytree(staged, published, dirs_exist_ok=True)
    (published / ".nojekyll").touch()
    # The artifact and regenerated Maven metadata are now in place.
    (published / "latest.txt").write_text(version + "\n")


if __name__ == "__main__":
    merge(Path(sys.argv[1]), Path(sys.argv[2]), sys.argv[3])
